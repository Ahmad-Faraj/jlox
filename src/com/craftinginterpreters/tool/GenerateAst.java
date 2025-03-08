package com.craftinginterpreters.tool;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign : Token name, Expr value",
                "Binary : Expr left, Token operator, Expr right",
                "Call : Expr callee, Token paren, List<Expr> arguments",
                "Grouping : Expr expression",
                "Literal : Object value",
                "Logical : Expr left, Token operator, Expr right",
                "Unary : Token operator, Expr right",
                "Variable : Token name"));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block : List<Stmt> statements",
                "Expression : Expr expression",
                "Function : Token name, List<Token> params," + " List<Stmt> body",
                "If : Expr condition, Stmt thenBranch, Stmt elseBranch",
                "Print : Expr expression",
                "Var : Token name, Expr initializer",
                "While : Expr condition, Stmt body",
                "Break :",
                "Continue :"));
    }

    private static void defineAst(String outputDir, String baseName, List<String> types) throws IOException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(
                new OutputStreamWriter(new FileOutputStream(path), StandardCharsets.UTF_8));

        writer.println("package com.craftinginterpreters.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String[] parts = type.split(":");
            String className = parts[0].trim();
            String fields = parts.length > 1 ? parts[1].trim() : "";
            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println(" abstract <R> R accept(Visitor<R> visitor);");
        writer.println("}");
        writer.close();
    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println(" interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println(" R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println(" }");
    }

    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println(" static class " + className + " extends " + baseName + " {");

        if (!fieldList.isEmpty()) {
            writer.println(" " + className + "(" + fieldList + ") {");
            String[] fields = fieldList.split(", ");
            for (String field : fields) {
                String name = field.split(" ")[1];
                writer.println(" this." + name + " = " + name + ";");
            }
            writer.println(" }");
        } else {
            writer.println(" " + className + "() {}");
        }

        writer.println();
        writer.println(" @Override");
        writer.println(" <R> R accept(Visitor<R> visitor) {");
        writer.println(" return visitor.visit" + className + baseName + "(this);");
        writer.println(" }");

        if (!fieldList.isEmpty()) {
            writer.println();
            for (String field : fieldList.split(", ")) {
                writer.println(" final " + field + ";");
            }
        }

        writer.println(" }");
    }
}
