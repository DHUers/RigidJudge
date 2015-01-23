all: sandbox

sandbox: sample2.c sandbox.o platform.o internal.o
	clang -pthread -lm sample2.c sandbox.o platform.o internal.o -o sandbox

sandbox.o: sandbox.c sandbox.h
	clang -c sandbox.c sandbox.h

platform.o: platform.c platform.h
	clang -c platform.c platform.h

internal.o: internal.c internal.h
	clang -c internal.c internal.h

clean:
	rm -rf *.o *.gch *.so
