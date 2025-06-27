package com.bigdata.recommendation;

import org.apache.spark.sql.SparkSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main application class for the Spark-based Recommendation Engine
 * 
 * This application demonstrates the three V's of Big Data:
 * - Volume: Processing large datasets efficiently
 * - Variety: Handling different data formats (CSV, JSON, etc.)
 * - Velocity: Real-time processing capabilities with Spark
 * 
 * Key advantages of Spark over traditional data processing:
 * 1. In-memory processing (100x faster than Hadoop MapReduce)
 * 2. Unified analytics engine for large-scale data processing
 * 3. Supports multiple programming languages (Java, Scala, Python, R)
 * 4. Built-in machine learning library (MLlib)
 * 5. Fault tolerance through RDD lineage
 * 6. Lazy evaluation for optimization
 */
public class RecommendationEngineApp {
    private static final Logger logger = LoggerFactory.getLogger(RecommendationEngineApp.class);
    
    public static void main(String[] args) {
        logger.info("Starting Spark Recommendation Engine Application");
        
        // Create SparkSession - entry point to all Spark functionality
        SparkSession spark = SparkSession.builder()
                .appName("E-Commerce Recommendation Engine")
                .master("local[*]") // Use all available cores locally
                .config("spark.sql.adaptive.enabled", "true")
                .config("spark.sql.adaptive.coalescePartitions.enabled", "true")
                .getOrCreate();
        
        try {
            // Set log level to reduce verbose output
            spark.sparkContext().setLogLevel("WARN");
            
            logger.info("Spark session created successfully");
            logger.info("Spark version: {}", spark.version());
            logger.info("Available cores: {}", spark.sparkContext().defaultParallelism());
            
            // Initialize recommendation engine components
            DataProcessor dataProcessor = new DataProcessor(spark);
            RecommendationEngine recommendationEngine = new RecommendationEngine(spark);
            
            // Process data and generate recommendations
            processRecommendations(dataProcessor, recommendationEngine);
            
        } catch (Exception e) {
            logger.error("Error in recommendation engine application", e);
        } finally {
            // Clean up resources
            spark.stop();
            logger.info("Spark session stopped");
        }
    }
    
    private static void processRecommendations(DataProcessor dataProcessor, 
                                             RecommendationEngine recommendationEngine) {
        logger.info("=== Starting Big Data Processing with Apache Spark ===");
        
        // Step 1: Load and explore data
        logger.info("Step 1: Loading and exploring data...");
        dataProcessor.loadAndExploreData();
        
        // Step 2: Process and analyze customer behavior
        logger.info("Step 2: Analyzing customer purchase behavior...");
        dataProcessor.analyzeCustomerBehavior();
        
        // Step 3: Generate content-based recommendations
        logger.info("Step 3: Generating content-based recommendations...");
        recommendationEngine.generateContentBasedRecommendations();
        
        // Step 4: Generate collaborative filtering recommendations
        logger.info("Step 4: Generating collaborative filtering recommendations...");
        recommendationEngine.generateCollaborativeRecommendations();
        
        // Step 5: Demonstrate big data capabilities
        logger.info("Step 5: Demonstrating big data processing capabilities...");
        demonstrateBigDataCapabilities(dataProcessor);
        
        logger.info("=== Recommendation Engine Processing Complete ===");
    }
    
    private static void demonstrateBigDataCapabilities(DataProcessor dataProcessor) {
        logger.info("\n=== BIG DATA CHARACTERISTICS DEMONSTRATION ===");
        
        // Volume: Show ability to process large datasets
        logger.info("VOLUME: Spark can handle petabytes of data across clusters");
        logger.info("- Current dataset size: {} customers, {} products", 
                   dataProcessor.getCustomerCount(), dataProcessor.getProductCount());
        logger.info("- Spark automatically partitions data across available cores");
        
        // Variety: Show different data format handling
        logger.info("\nVARIETY: Spark supports multiple data formats");
        logger.info("- CSV files (structured data)");
        logger.info("- JSON, Parquet, Avro (semi-structured)");
        logger.info("- Text files, images, streaming data (unstructured)");
        
        // Velocity: Show real-time processing capabilities
        logger.info("\nVELOCITY: Spark enables real-time processing");
        logger.info("- Spark Streaming for real-time data processing");
        logger.info("- In-memory computing for sub-second query responses");
        logger.info("- Micro-batch processing for near real-time analytics");
        
        logger.info("\n=== SPARK ADVANTAGES OVER TRADITIONAL TOOLS ===");
        logger.info("1. Speed: 100x faster than Hadoop MapReduce (in-memory)");
        logger.info("2. Ease of Use: High-level APIs in multiple languages");
        logger.info("3. Generality: Unified engine for batch, streaming, ML, graph processing");
        logger.info("4. Runs Everywhere: Hadoop, Mesos, Kubernetes, standalone, cloud");
    }
}
