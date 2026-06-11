'use strict';

/**
 * CSV to Parquet Converter
 * 
 * Usage: ./rec script csv2parquet.js <input.csv> <output.parquet> [delimiter] [columns]
 * 
 * Arguments:
 *   input.csv     - Path to the input CSV file
 *   output.parquet - Path to the output Parquet file
 *   delimiter     - (Optional) CSV delimiter, defaults to ","
 *   columns       - (Optional) Comma-separated column names (header), e.g., "name,age,city"
 * 
 * Examples:
 *   ./rec script csv2parquet.js data.csv data.parquet
 *   ./rec script csv2parquet.js data.csv data.parquet "\t" "id,name,value"
 */

const {csv, file, println, target} = require("rec");

// Parse command-line arguments
var args = Array.prototype.slice.call(arguments || []);

if (args.length < 2) {
    println("Usage: rec script csv2parquet.js <input.csv> <output.parquet> [delimiter] [columns]");
    println("");
    println("Examples:");
    println("  rec script csv2parquet.js data.csv data.parquet");
    println("  rec script csv2parquet.js data.csv data.parquet \"\\t\" \"id,name,value\"");
    java.lang.System.exit(1);
}

var inputFile = args[0];
var outputFile = args[1];
var delimiter = args.length > 2 ? args[2] : ",";
var columns = args.length > 3 ? args[3] : null;

println("Converting CSV to Parquet:");
println("  Input:      " + inputFile);
println("  Output:     " + outputFile);
println("  Delimiter:  " + delimiter);
if (columns) {
    println("  Columns:    " + columns);
}

// If columns not provided, read the first line of the CSV to get headers
if (!columns) {
    var BufferedReader = java.io.BufferedReader;
    var FileReader = java.io.FileReader;
    
    try {
        var reader = new BufferedReader(new FileReader(inputFile));
        var headerLine = reader.readLine();
        reader.close();
        
        if (headerLine) {
            columns = headerLine;
            println("  Auto-detected columns: " + columns);
        } else {
            println("Error: Could not read header from CSV file");
            java.lang.System.exit(1);
        }
    } catch (e) {
        println("Error reading CSV header: " + e.message);
        java.lang.System.exit(1);
    }
}

// Open the input CSV file and create the Parquet target
var csvSource = csv(file(inputFile), delimiter, columns);

// Use the parquet plugin to create a Parquet target
var parquetTarget = __parquet.parquetTarget(new java.io.File(outputFile));

// Transform CSV to Parquet - pipe all records to the parquet target
csvSource.to(parquetTarget);

println("Done! Parquet file created at: " + outputFile);
