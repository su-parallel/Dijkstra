all:
	@javac ParallelizedVersion.java SerializedVersion.java


clean:
	@rm -f *.class
