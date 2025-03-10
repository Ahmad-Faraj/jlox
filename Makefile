JAVAC = javac
JAVA = java
SRC = src/lox/*.java
BIN = bin

all: compile

compile:
	mkdir -p $(BIN)
	$(JAVAC) -d $(BIN) $(SRC)

run:
	$(JAVA) -cp $(BIN) lox.Lox

tool:
	javac -d $(BIN) src/tool/GenerateAst.java
	java -cp $(BIN) tool.GenerateAst src/lox

clean:
	rm -rf $(BIN)
