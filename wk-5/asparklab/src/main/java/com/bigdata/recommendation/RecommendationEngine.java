package com.bigdata.recommendation;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.*;

/**
 * RecommendationEngine implements different recommendation algorithms using Spark
 * 
 * Implemented algorithms:
 * 1. Content-based filtering: Recommends items based on item features
 * 2. Collaborative filtering: Recommends items based on user behavior patterns
 * 3. Hybrid approaches: Combines multiple recommendation strategies
 * 
 * Demonstrates Spark MLlib capabilities for machine learning at scale
 */
public class RecommendationEngine {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngine.class);
    
    private final SparkSession spark;
    private Dataset<Row> customersDF;
    private Dataset<Row> productsDF;
    private Dataset<Row> purchasesDF;
    
    public RecommendationEngine(SparkSession spark) {
        this.spark = spark;
        loadData();
    }
    
    private void loadData() {
        // Load data from CSV files
        customersDF = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("src/main/resources/customers.csv");
        
        productsDF = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("src/main/resources/products.csv");
        
        purchasesDF = spark.read()
                .option("header", "true")
                .option("inferSchema", "true")
                .csv("src/main/resources/purchases.csv");
        
        // Create temporary views for SQL operations
        customersDF.createOrReplaceTempView("customers");
        productsDF.createOrReplaceTempView("products");
        purchasesDF.createOrReplaceTempView("purchases");
    }
    
    /**
     * Generate content-based recommendations
     * Recommends products based on similar characteristics (category, brand, price range)
     */
    public void generateContentBasedRecommendations() {
        logger.info("\n=== CONTENT-BASED RECOMMENDATIONS ===");
        
        // For each customer, recommend products from categories they've purchased
        generateCategoryBasedRecommendations();
        
        // Brand affinity recommendations
        generateBrandAffinityRecommendations();
        
        // Price-based recommendations
        generatePriceBasedRecommendations();
    }
    
    private void generateCategoryBasedRecommendations() {
        logger.info("\n--- Category-Based Recommendations ---");
        
        // Find customer's preferred categories
        Dataset<Row> customerCategories = spark.sql(
            "SELECT c.customer_id, c.name, p.category, " +
            "       COUNT(*) as category_purchases, " +
            "       AVG(p.price) as avg_spent_in_category " +
            "FROM customers c " +
            "JOIN purchases pu ON c.customer_id = pu.customer_id " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "GROUP BY c.customer_id, c.name, p.category " +
            "ORDER BY c.customer_id, category_purchases DESC"
        );
        
        customerCategories.show(20);
        
        // Generate recommendations for top 3 customers
        logger.info("\nRecommendations for top customers based on their preferred categories:");
        
        Dataset<Row> recommendations = spark.sql(
            "WITH customer_preferences AS ( " +
            "    SELECT customer_id, category, " +
            "           ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY COUNT(*) DESC) as rank " +
            "    FROM purchases pu " +
            "    JOIN products p ON pu.product_id = p.product_id " +
            "    GROUP BY customer_id, category " +
            "), " +
            "customer_purchased AS ( " +
            "    SELECT DISTINCT customer_id, product_id " +
            "    FROM purchases " +
            ") " +
            "SELECT c.customer_id, cu.name, cp.category, p.product_id, p.name as product_name, p.price " +
            "FROM customer_preferences cp " +
            "JOIN customers cu ON cp.customer_id = cu.customer_id " +
            "JOIN products p ON cp.category = p.category " +
            "LEFT JOIN customer_purchased purchased ON cp.customer_id = purchased.customer_id " +
            "                                        AND p.product_id = purchased.product_id " +
            "WHERE cp.rank = 1 AND purchased.product_id IS NULL " +
            "      AND cp.customer_id <= 3 " +
            "ORDER BY cp.customer_id, p.price DESC " +
            "LIMIT 15"
        );
        
        recommendations.show();
    }
    
    private void generateBrandAffinityRecommendations() {
        logger.info("\n--- Brand Affinity Recommendations ---");
        
        Dataset<Row> brandAffinity = spark.sql(
            "SELECT c.customer_id, c.name, p.brand, " +
            "       COUNT(*) as brand_purchases, " +
            "       SUM(p.price) as total_spent_on_brand " +
            "FROM customers c " +
            "JOIN purchases pu ON c.customer_id = pu.customer_id " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "GROUP BY c.customer_id, c.name, p.brand " +
            "HAVING brand_purchases > 1 " +
            "ORDER BY c.customer_id, brand_purchases DESC"
        );
        
        brandAffinity.show();
    }
    
    private void generatePriceBasedRecommendations() {
        logger.info("\n--- Price Range Based Recommendations ---");
        
        // Categorize customers by their spending patterns
        Dataset<Row> customerSpendingProfiles = spark.sql(
            "SELECT c.customer_id, c.name, " +
            "       AVG(p.price) as avg_purchase_price, " +
            "       MIN(p.price) as min_price, " +
            "       MAX(p.price) as max_price, " +
            "       CASE " +
            "           WHEN AVG(p.price) < 50 THEN 'Budget' " +
            "           WHEN AVG(p.price) < 200 THEN 'Mid-range' " +
            "           ELSE 'Premium' " +
            "       END as spending_category " +
            "FROM customers c " +
            "JOIN purchases pu ON c.customer_id = pu.customer_id " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "GROUP BY c.customer_id, c.name " +
            "ORDER BY avg_purchase_price DESC"
        );
        
        customerSpendingProfiles.show();
    }
    
    /**
     * Generate collaborative filtering recommendations
     * Uses user-item interaction patterns to find similar users and recommend items
     */
    public void generateCollaborativeRecommendations() {
        logger.info("\n=== COLLABORATIVE FILTERING RECOMMENDATIONS ===");
        
        // User-based collaborative filtering
        generateUserBasedRecommendations();
        
        // Item-based collaborative filtering
        generateItemBasedRecommendations();
        
        // Matrix factorization approach (simplified)
        generateMatrixFactorizationRecommendations();
    }
    
    private void generateUserBasedRecommendations() {
        logger.info("\n--- User-Based Collaborative Filtering ---");
        
        // Find customers with similar purchase patterns
        Dataset<Row> similarCustomers = spark.sql(
            "WITH customer_categories AS ( " +
            "    SELECT customer_id, p.category, COUNT(*) as purchases " +
            "    FROM purchases pu " +
            "    JOIN products p ON pu.product_id = p.product_id " +
            "    GROUP BY customer_id, p.category " +
            "), " +
            "customer_pairs AS ( " +
            "    SELECT c1.customer_id as customer1, c2.customer_id as customer2, " +
            "           COUNT(*) as common_categories " +
            "    FROM customer_categories c1 " +
            "    JOIN customer_categories c2 ON c1.category = c2.category " +
            "    WHERE c1.customer_id < c2.customer_id " +
            "    GROUP BY c1.customer_id, c2.customer_id " +
            "    HAVING common_categories >= 2 " +
            ") " +
            "SELECT cu1.name as customer1_name, cu2.name as customer2_name, " +
            "       cp.common_categories " +
            "FROM customer_pairs cp " +
            "JOIN customers cu1 ON cp.customer1 = cu1.customer_id " +
            "JOIN customers cu2 ON cp.customer2 = cu2.customer_id " +
            "ORDER BY cp.common_categories DESC " +
            "LIMIT 10"
        );
        
        similarCustomers.show();
    }
    
    private void generateItemBasedRecommendations() {
        logger.info("\n--- Item-Based Collaborative Filtering ---");
        
        // Find products frequently bought together
        Dataset<Row> frequentlyBoughtTogether = spark.sql(
            "WITH customer_products AS ( " +
            "    SELECT customer_id, COLLECT_LIST(product_id) as products " +
            "    FROM purchases " +
            "    GROUP BY customer_id " +
            "    HAVING SIZE(COLLECT_LIST(product_id)) > 1 " +
            "), " +
            "product_pairs AS ( " +
            "    SELECT p1.product_id as product1, p2.product_id as product2 " +
            "    FROM ( " +
            "        SELECT customer_id, EXPLODE(products) as product_id " +
            "        FROM customer_products " +
            "    ) p1 " +
            "    JOIN ( " +
            "        SELECT customer_id, EXPLODE(products) as product_id " +
            "        FROM customer_products " +
            "    ) p2 ON p1.customer_id = p2.customer_id " +
            "    WHERE p1.product_id < p2.product_id " +
            ") " +
            "SELECT pr1.name as product1_name, pr2.name as product2_name, " +
            "       COUNT(*) as bought_together_count " +
            "FROM product_pairs pp " +
            "JOIN products pr1 ON pp.product1 = pr1.product_id " +
            "JOIN products pr2 ON pp.product2 = pr2.product_id " +
            "GROUP BY pr1.name, pr2.name " +
            "ORDER BY bought_together_count DESC " +
            "LIMIT 15"
        );
        
        frequentlyBoughtTogether.show();
    }
    
    private void generateMatrixFactorizationRecommendations() {
        logger.info("\n--- Matrix Factorization Approach ---");
        
        // Create user-item rating matrix (using purchase frequency as implicit rating)
        Dataset<Row> userItemMatrix = spark.sql(
            "SELECT customer_id, product_id, " +
            "       COUNT(*) as purchase_frequency, " +
            "       SUM(p.price) as total_spent " +
            "FROM purchases pu " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "GROUP BY customer_id, product_id " +
            "ORDER BY customer_id, purchase_frequency DESC"
        );
        
        logger.info("User-Item Interaction Matrix (implicit ratings):");
        userItemMatrix.show(20);
        
        // Calculate product popularity scores
        Dataset<Row> popularityScores = spark.sql(
            "SELECT p.product_id, p.name, p.category, " +
            "       COUNT(pu.customer_id) as total_customers, " +
            "       COUNT(pu.purchase_id) as total_purchases, " +
            "       AVG(p.price) as avg_price, " +
            "       (COUNT(pu.customer_id) * 0.7 + COUNT(pu.purchase_id) * 0.3) as popularity_score " +
            "FROM products p " +
            "LEFT JOIN purchases pu ON p.product_id = pu.product_id " +
            "GROUP BY p.product_id, p.name, p.category, p.price " +
            "ORDER BY popularity_score DESC"
        );
        
        logger.info("\nProduct Popularity Rankings:");
        popularityScores.show();
    }
    
    /**
     * Generate personalized recommendations for a specific customer
     */
    public void generatePersonalizedRecommendations(int customerId) {
        logger.info("\n=== PERSONALIZED RECOMMENDATIONS FOR CUSTOMER {} ===", customerId);
        
        // Get customer's purchase history
        Dataset<Row> customerHistory = spark.sql(
            "SELECT c.name, p.product_id, p.name as product_name, " +
            "       p.category, p.brand, p.price, pu.purchase_date " +
            "FROM customers c " +
            "JOIN purchases pu ON c.customer_id = pu.customer_id " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "WHERE c.customer_id = " + customerId + " " +
            "ORDER BY pu.purchase_date DESC"
        );
        
        logger.info("Customer's Purchase History:");
        customerHistory.show();
        
        // Generate hybrid recommendations
        Dataset<Row> hybridRecommendations = spark.sql(
            "WITH customer_profile AS ( " +
            "    SELECT customer_id, p.category, p.brand, AVG(p.price) as avg_price " +
            "    FROM purchases pu " +
            "    JOIN products p ON pu.product_id = p.product_id " +
            "    WHERE customer_id = " + customerId + " " +
            "    GROUP BY customer_id, p.category, p.brand " +
            "), " +
            "purchased_products AS ( " +
            "    SELECT DISTINCT product_id " +
            "    FROM purchases " +
            "    WHERE customer_id = " + customerId + " " +
            ") " +
            "SELECT p.product_id, p.name, p.category, p.brand, p.price, " +
            "       CASE " +
            "           WHEN cp.category IS NOT NULL THEN 'Category Match' " +
            "           WHEN cp.brand IS NOT NULL THEN 'Brand Match' " +
            "           ELSE 'Popular Item' " +
            "       END as recommendation_reason " +
            "FROM products p " +
            "LEFT JOIN customer_profile cp ON p.category = cp.category OR p.brand = cp.brand " +
            "LEFT JOIN purchased_products pp ON p.product_id = pp.product_id " +
            "WHERE pp.product_id IS NULL " +
            "ORDER BY (CASE WHEN cp.category IS NOT NULL THEN 3 " +
            "              WHEN cp.brand IS NOT NULL THEN 2 " +
            "              ELSE 1 END) DESC, p.price ASC " +
            "LIMIT 10"
        );
        
        logger.info("Hybrid Recommendations:");
        hybridRecommendations.show();
    }
    
    /**
     * Evaluate recommendation system performance
     */
    public void evaluateRecommendationSystem() {
        logger.info("\n=== RECOMMENDATION SYSTEM EVALUATION ===");
        
        // Calculate system metrics
        Dataset<Row> systemMetrics = spark.sql(
            "SELECT " +
            "    COUNT(DISTINCT customer_id) as total_customers, " +
            "    COUNT(DISTINCT product_id) as total_products, " +
            "    COUNT(*) as total_interactions, " +
            "    AVG(interactions_per_customer) as avg_interactions_per_customer, " +
            "    (COUNT(DISTINCT customer_id) * COUNT(DISTINCT product_id)) as matrix_size, " +
            "    (COUNT(*) * 100.0 / (COUNT(DISTINCT customer_id) * COUNT(DISTINCT product_id))) as sparsity " +
            "FROM ( " +
            "    SELECT customer_id, COUNT(*) as interactions_per_customer " +
            "    FROM purchases " +
            "    GROUP BY customer_id " +
            ") customer_stats " +
            "CROSS JOIN purchases"
        );
        
        systemMetrics.show();
        
        // Category coverage analysis
        Dataset<Row> categoryCoverage = spark.sql(
            "SELECT " +
            "    COUNT(DISTINCT p.category) as categories_with_purchases, " +
            "    (SELECT COUNT(DISTINCT category) FROM products) as total_categories, " +
            "    (COUNT(DISTINCT p.category) * 100.0 / (SELECT COUNT(DISTINCT category) FROM products)) as category_coverage " +
            "FROM purchases pu " +
            "JOIN products p ON pu.product_id = p.product_id"
        );
        
        categoryCoverage.show();
        
        logger.info("Recommendation system evaluation complete!");
    }
}
