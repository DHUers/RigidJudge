#include <time.h>
#include <stdio.h>
#include <ctype.h>
#include <errno.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <stdarg.h>
#include <unistd.h>
#include <sys/wait.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <sys/user.h>
#include <sys/types.h>
#include <sys/signal.h>
#include <sys/ptrace.h>
#include <sys/syscall.h>
#include <sys/resource.h>

#define KB 1024
#define MB (KB*KB)
#define BUFFER_SIZE 512

/*
* UN：初始值
* RK：子进程运行时被cpu给kill了 ，原因TLE，MLE
* OLE：发生ole时返回SIGFSZ 由父进程去kill子进程
* RS：子进程运行顺利 OK
* */
enum {UN = 0, RS, RK, OK, RE, TLE, MLE, OLE, OTHER};

int result = UN, time_limit = 1000, memory_limit = 65535*KB;

void printSignal(int signo) {
    switch (signo) {
        case 5:
            puts("SIGTRAP");
            break;
        case 8:
            puts("SIGFEP");
            break;  // div 0
        case 9:
            puts("SIGKILL");
            break;  // been killed
        case 11:
            puts("SIGSEGV");
            break;  // yue jie
        case 14:
            puts("SIGALRM");
            break;
        case 15:
            puts("SIGTERM");
            break;
        case 17:
            puts("SIGCHLD");
            break;
        case 24:
            puts("SIGXCPU");
            break;
        case 25:
            puts("SIGXFSZ");
            break;
        default:
            puts("unknow");
            break;
        /*
        case 3:
            puts("SIGQUIT");
            break;
        case 4:
            puts("SIGILL");
            break;
        case 5:
            puts("TRAP");
            break;
        case 6:
            puts("ABRT");
            break;
        case 7:
            puts("BUS");
            break;
        case 8:
            puts("FEP");
            break;
        case 9:
            puts("KILL");
            break;
        case 10:
            puts("USR1");
            break;
        case 11:
            puts("SEGV");
            break;
        case 12:
            puts("USR2");
            break;
        case 13:
            puts("PIPE");
            break;
        case 14:
            puts("ALRM");
            break;
        case 15:
            puts("TERM");
            break;
        case 16:
            puts("STKFLT");
            break;
        case 17:
            puts("CHLD");
            break;
        case 18:
            puts("CONT");
            break;
        case 19:
            puts("STOP");
            break;
        case 20:
            puts("TSTP");
            break;
        case 21:
            puts("TTIN");
            break;
        case 22:
            puts("TTOUT");
            break;
        case 23:
            puts("URG");
            break;
        case 24:
            puts("XCPU");
            break;
        case 25:
            puts("XFSZ");
            break;
        case 26:
            puts("VTALRM");
            break;
        default:
            printf("unkoow signo = %d\n", signo);
        */
    }
}

void printResult(int result) {
    printf("result:");
    switch (result) {
        case OK:
            printf("OK\t");
            break;
        case RE:
            printf("RE\t");
            break;
        case TLE:
            printf("TLE\t");
            break;
        case MLE:
            printf("MLE\t");
            break;
        case OLE:
            printf("OLE\t");
            break;
        default:
            printf("notice! result=%d", result);
    }
}

inline void get_time_usage(int *p_time_used, struct rusage *pusage) {
    *p_time_used += (pusage->ru_utime.tv_sec * 1000 + pusage->ru_utime.tv_usec / 1000);
    *p_time_used += (pusage->ru_stime.tv_sec * 1000 + pusage->ru_stime.tv_usec / 1000);
}

inline int get_proc_status(int pid, const char *mark) {
    char fn[BUFFER_SIZE], buf[BUFFER_SIZE];
    int ret = 0;
    sprintf(fn, "/proc/%d/status", pid);
    FILE *pf = fopen(fn, "r");
    int m = strlen(mark);
    while (pf && fgets(buf, BUFFER_SIZE - 1, pf)) {
        buf[strlen(buf) - 1] = 0;
        if (strncmp(buf, mark, m) == 0) {
            sscanf(buf + m + 1, "%d", &ret);
        }
    }
    if (pf) fclose(pf);
    return ret;
}

inline int get_mem_usage(int pid) {
    return get_proc_status(pid, "VmPeak:");
}

inline int compile(char *filename) {  // for Debug only
    pid_t cpcld = fork();
    if (cpcld == 0) {
        execlp("g++", "g++", filename, (char*)NULL);
        perror("excelp");
        return 0;
    }
    int stat;
    pid_t w = waitpid(cpcld, &stat, WUNTRACED);
    if (w == -1) {
        perror("waitpid");
        return 0;
    }
    if (WIFEXITED(stat)) {
        switch (WEXITSTATUS(stat)) {
            case EXIT_SUCCESS:
                puts("compile success");
                return 1;
            default:
                puts("compile error");
                return 0;
        }
    } else {
        puts("compile is not exit success");
        return 0;
    }
}

int main(int argc, char *argv[]) {
    if (argc < 2) {
        puts("Usage: sandbox FILE [TIME_LIMIT] [MEMORY_LIMIT] [OPTION]");
        return -1;
    }
    if (argc > 2) {
        time_limit = atoi(argv[2]);
    }
    if (argc > 3) {
        memory_limit = atoi(argv[3]);
    }
    printf("%d\t%d\n", time_limit, memory_limit);

    if (!compile(argv[1])) {
        return 1;
    }

    pid_t runcld = fork();
    if (runcld == 0) {
        struct rlimit rlmt;
        rlmt.rlim_cur = rlmt.rlim_max = 1;
        if (0 != setrlimit(RLIMIT_CPU, &rlmt)) {
            perror("setrlimit");
        }

        rlmt.rlim_cur = rlmt.rlim_max = memory_limit;
        if (setrlimit(RLIMIT_AS, &rlmt) < 0) perror("setrlimit");

        rlmt.rlim_cur = rlmt.rlim_max = 6 * MB;
        if (setrlimit(RLIMIT_STACK, &rlmt) < 0) perror("setrlimit");

        rlmt.rlim_cur = rlmt.rlim_max = 2 * MB;
        if (setrlimit(RLIMIT_FSIZE, &rlmt) < 0) perror("setrlimit");

        ptrace(PTRACE_TRACEME, 0, NULL, NULL);
        //alarm(1);
        execl("./a.out", "a.out", (char*)NULL);
    }

    //int t = 0;
    int stat;
    int time_usage = 0, mem_usage = 0, cur_mem = 0;
    struct rusage rused;
    int signo = -1;
    int pagesize = getpagesize();
    ptrace(PTRACE_SYSCALL, runcld, NULL, NULL);
    while (1) {
        if (-1 == wait4(runcld, &stat, WUNTRACED, &rused)) {
            perror("waitpid");
            return 0;
        }

        get_time_usage(&time_usage, &rused);

        // child成功运行并调用了exit
        if (WIFEXITED(stat)) {
            switch (WEXITSTATUS(stat)) {
            case EXIT_SUCCESS:
                result = RS;
                puts("run exit success");
                break;
            default:
                result = RE;
                puts("run exit unsuccess");
                break;
            }
            break;
        }
        // 由于某种原因而child进程停止运行，原因如下：
        // 1. re 遇到这些情况时child进程已经被停止运行
        // 2. 由于跟踪选项PTRACE_SYSCALL，每次系统调用都会返回SIGTRAP信号，表示占停
        if (WIFSTOPPED(stat)) {
            signo = WSTOPSIG(stat);
            if (signo != SIGTRAP) {
                printf("STOPPED signo %d ", signo);
                printSignal(signo);
            }
            switch (signo) {
                case SIGTRAP:  // 情况2：继续发出跟踪child的系统调用
                    // 对孩子进程进行必要的处理
                    ptrace(PTRACE_SYSCALL, runcld, NULL, NULL);
                    break;
                case SIGFPE:  // 运算异常
                case SIGSEGV:  // 无效内存引用：越界,栈溢出
                    result = RE;
                    ptrace(PTRACE_KILL, runcld, NULL, NULL);
                    break;
                case SIGXFSZ:  // OLE
                    result = OLE;
                    ptrace(PTRACE_KILL, runcld, NULL, NULL);
                    break;
                case SIGCHLD:  // 子进程被挂起，感觉这是有问题的
                    result = RS;
                    ptrace(PTRACE_KILL, runcld, NULL, NULL);
                    break;
            }
        }
        // 1.接受了：SIGKILL
        // 2.tle,mle
        if (WIFSIGNALED(stat)) {
            signo = WTERMSIG(stat);
            printf("SIGNALED slp signo %d ", signo);
            printSignal(signo);
            switch (signo) {
            case SIGKILL://TLE,MLE
                if (result == UN) result = RK;
                break;
            }
            break;
        }

        cur_mem = get_mem_usage(runcld) * pagesize;
        if (mem_usage < cur_mem) mem_usage = cur_mem;
        if (mem_usage / MB + 3 > memory_limit / MB) {  //误差设置为3MB
            result = MLE;
            ptrace(PTRACE_KILL, runcld, NULL, NULL);
        }
    }

    if (result == RS) {
        result = OK;
    } else if (result == RK) {
        if (time_usage >= time_limit - 100) {
            time_usage = time_limit;
            result = TLE;
        } else if (mem_usage >= memory_limit - 100) {
            mem_usage = memory_limit;
            result = MLE;
        }
    }

    printResult(result);
    printf("time_usage: %dms\n mem_used: %dKB\n", time_usage, mem_usage / KB + 1);
    return result;
}
