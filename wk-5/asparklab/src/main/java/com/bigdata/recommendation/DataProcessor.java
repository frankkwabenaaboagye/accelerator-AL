package com.bigdata.recommendation;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.functions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.spark.sql.functions.*;

/**
 * DataProcessor handles all data loading, cleaning, and basic analysis operations
 * 
 * Demonstrates key big data processing capabilities:
 * - Loading data from various sources (CSV, JSON, databases)
 * - Data cleaning and transformation
 * - Aggregations and analytics
 * - Data quality checks
 */
public class DataProcessor {
    private static final Logger logger = LoggerFactory.getLogger(DataProcessor.class);
    
    private final SparkSession spark;
    private Dataset<Row> customersDF;
    private Dataset<Row> productsDF;
    private Dataset<Row> purchasesDF;
    
    public DataProcessor(SparkSession spark) {
        this.spark = spark;
    }
    
    /**
     * Load data from CSV files and perform initial exploration
     */
    public void loadAndExploreData() {
        logger.info("Loading datasets from CSV files...");
        
        try {
            // Load customers data
            customersDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv("src/main/resources/customers.csv");
            
            // Load products data
            productsDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv("src/main/resources/products.csv");
            
            // Load purchases data
            purchasesDF = spark.read()
                    .option("header", "true")
                    .option("inferSchema", "true")
                    .csv("src/main/resources/purchases.csv");
            
            // Cache datasets for better performance
            customersDF.cache();
            productsDF.cache();
            purchasesDF.cache();
            
            // Display basic information about datasets
            exploreDatasets();
            
        } catch (Exception e) {
            logger.error("Error loading data", e);
            throw new RuntimeException("Failed to load data", e);
        }
    }
    
    /**
     * Explore and display basic statistics about the loaded datasets
     */
    private void exploreDatasets() {
        logger.info("\n=== DATASET EXPLORATION ===");
        
        // Customers dataset
        logger.info("Customers Dataset:");
        logger.info("- Records: {}", customersDF.count());
        logger.info("- Columns: {}", String.join(", ", customersDF.columns()));
        customersDF.show(5);
        customersDF.printSchema();
        
        // Products dataset
        logger.info("\nProducts Dataset:");
        logger.info("- Records: {}", productsDF.count());
        logger.info("- Columns: {}", String.join(", ", productsDF.columns()));
        productsDF.show(5);
        
        // Purchases dataset
        logger.info("\nPurchases Dataset:");
        logger.info("- Records: {}", purchasesDF.count());
        logger.info("- Columns: {}", String.join(", ", purchasesDF.columns()));
        purchasesDF.show(5);
        
        // Show data quality information
        checkDataQuality();
    }
    
    /**
     * Perform data quality checks
     */
    private void checkDataQuality() {
        logger.info("\n=== DATA QUALITY CHECKS ===");
        
        // Check for null values
        logger.info("Checking for null values...");
        for (String column : customersDF.columns()) {
            long nullCount = customersDF.filter(col(column).isNull()).count();
            if (nullCount > 0) {
                logger.warn("Column '{}' has {} null values", column, nullCount);
            }
        }
        
        // Check for duplicate customers
        long totalCustomers = customersDF.count();
        long uniqueCustomers = customersDF.select("customer_id").distinct().count();
        if (totalCustomers != uniqueCustomers) {
            logger.warn("Found {} duplicate customer records", totalCustomers - uniqueCustomers);
        } else {
            logger.info("No duplicate customer records found");
        }
        
        // Validate data ranges
        logger.info("Customer age range: {} to {}", 
                   customersDF.agg(min("age")).first().getLong(0),
                   customersDF.agg(max("age")).first().getLong(0));
        
        logger.info("Product price range: ${} to ${}", 
                   productsDF.agg(min("price")).first().getDouble(0),
                   productsDF.agg(max("price")).first().getDouble(0));
    }
    
    /**
     * Analyze customer purchase behavior using Spark SQL and DataFrame operations
     */
    public void analyzeCustomerBehavior() {
        logger.info("\n=== CUSTOMER BEHAVIOR ANALYSIS ===");
        
        // Create temporary views for SQL queries
        customersDF.createOrReplaceTempView("customers");
        productsDF.createOrReplaceTempView("products");
        purchasesDF.createOrReplaceTempView("purchases");
        
        // Analyze purchase patterns by category
        analyzePurchasesByCategory();
        
        // Find top spending customers
        findTopSpendingCustomers();
        
        // Analyze customer demographics
        analyzeCustomerDemographics();
        
        // Product popularity analysis
        analyzeProductPopularity();
        
        // Monthly purchase trends
        analyzePurchaseTrends();
    }
    
    private void analyzePurchasesByCategory() {
        logger.info("\n--- Purchase Analysis by Category ---");
        
        Dataset<Row> categoryAnalysis = spark.sql(
            "SELECT p.category, " +
            "       COUNT(*) as total_purchases, " +
            "       AVG(p.price) as avg_price, " +
            "       SUM(p.price) as total_revenue " +
            "FROM purchases pu " +
            "JOIN products p ON pu.product_id = p.product_id " +
            "GROUP BY p.category " +
            "ORDER BY total_revenue DESC"
        );
        
        categoryAnalysis.show();
    }
    
    private void findTopSpendingCustomers() {
        logger.info("\n--- Top 10 Spending Customers ---");
        
        Dataset<Row> topCustomers = customersDF
                .join(purchasesDF, "customer_id")
                .join(productsDF, "product_id")
                .groupBy("customer_id", "name")
                .agg(
                    sum("price").alias("total_spent"),
                    count("purchase_id").alias("total_purchases"),
                    avg("price").alias("avg_purchase_value")
                )
                .orderBy(desc("total_spent"))
                .limit(10);
        
        topCustomers.show();
    }
    
    private void analyzeCustomerDemographics() {
        logger.info("\n--- Customer Demographics Analysis ---");
        
        // Age distribution
        customersDF
                .groupBy("location")
                .agg(
                    count("customer_id").alias("customer_count"),
                    avg("age").alias("avg_age")
                )
                .orderBy(desc("customer_count"))
                .show();
        
        // Age groups
        customersDF
                .withColumn("age_group", 
                    when(col("age").lt(25), "18-24")
                    .when(col("age").lt(35), "25-34")
                    .when(col("age").lt(45), "35-44")
                    .otherwise("45+"))
                .groupBy("age_group")
                .count()
                .orderBy("age_group")
                .show();
    }
    
    private void analyzeProductPopularity() {
        logger.info("\n--- Product Popularity Analysis ---");
        
        Dataset<Row> productPopularity = productsDF
                .join(purchasesDF, "product_id")
                .groupBy("product_id", "name", "category", "brand")
                .agg(
                    count("purchase_id").alias("purchase_count"),
                    first("price").alias("price")
                )
                .orderBy(desc("purchase_count"))
                .limit(10);
        
        productPopularity.show();
    }
    
    private void analyzePurchaseTrends() {
        logger.info("\n--- Monthly Purchase Trends ---");
        
        Dataset<Row> monthlyTrends = purchasesDF
                .withColumn("month", date_format(col("purchase_date"), "yyyy-MM"))
                .join(productsDF, "product_id")
                .groupBy("month")
                .agg(
                    count("purchase_id").alias("total_purchases"),
                    sum("price").alias("total_revenue"),
                    countDistinct("customer_id").alias("unique_customers")
                )
                .orderBy("month");
        
        monthlyTrends.show();
    }
    
    // Getter methods for accessing processed data
    public Dataset<Row> getCustomersDF() { return customersDF; }
    public Dataset<Row> getProductsDF() { return productsDF; }
    public Dataset<Row> getPurchasesDF() { return purchasesDF; }
    
    public long getCustomerCount() {
        return customersDF != null ? customersDF.count() : 0;
    }
    
    public long getProductCount() {
        return productsDF != null ? productsDF.count() : 0;
    }
    
    /**
     * Get customer-product interaction matrix for collaborative filtering
     */
    public Dataset<Row> getCustomerProductMatrix() {
        return customersDF
                .join(purchasesDF, "customer_id")
                .join(productsDF, "product_id")
                .select("customer_id", "product_id", "price")
                .groupBy("customer_id", "product_id")
                .agg(sum("price").alias("total_spent"));
    }
}
