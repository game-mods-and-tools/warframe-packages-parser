test:
	javac *.java
	java Parser zzz.bin > out.json
	node test.js
