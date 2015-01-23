all: sandbox team_dhuacm_RigidJudge_local_Sandbox

sandbox: sample2.c sandbox.o platform.o internal.o
	clang -pthread -lm sample2.c sandbox.o platform.o internal.o -o sandbox

sandbox.o: sandbox.c sandbox.h
	clang -c sandbox.c sandbox.h

platform.o: platform.c platform.h
	clang -c platform.c platform.h

internal.o: internal.c internal.h
	clang -c internal.c internal.h

team_dhuacm_RigidJudge_local_Sandbox: team_dhuacm_RigidJudge_local_Sandbox.o
	clang -shared team_dhuacm_RigidJudge_local_Sandbox.o -o sandbox.so

team_dhuacm_RigidJudge_local_Sandbox.o: team_dhuacm_RigidJudge_local_Sandbox.c
	clang -c team_dhuacm_RigidJudge_local_Sandbox.c -I$$JAVA_HOME/include -fPIC

clean:
	rm -rf *.o *.gch *.so
