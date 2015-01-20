#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <dirent.h>
#include <unistd.h>
#include <time.h>
#include <stdarg.h>
#include <ctype.h>
#include <sys/wait.h>
#include <sys/ptrace.h>
#include <sys/types.h>
#include <sys/user.h>
#include <sys/syscall.h>
#include <sys/time.h>
#include <sys/resource.h>
#include <sys/signal.h>
#include <sys/stat.h>
#include <unistd.h>
#include <errno.h>

#define KB 1024
#define MB (KB*KB)
#define MEM_LMT 32*MB

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

/*
 * UN：初始值
 * RK：子进程运行时被cpu给kill了 ，原因TLE，MLE
 * OLE：发生ole时返回SIGFSZ 由父进程去kill子进程
 * RS：子进程运行顺利 AC PE WA
 * */
enum {UN = 0, RS, RK, AC, PE, WA, RE, TLE, MLE, OLE, OTHER};

void printResult(int result) {
    printf("result:");
    switch (result) {
        case AC:
            printf("AC\t");
            break;
        case WA:
            printf("WA\t");
            break;
        case PE:
            printf("PE\t");
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

inline long get_mem_usage(int pid) {
    char path[255];
    FILE *fp;
    long vsize = 0;
    sprintf(path, "/proc/%d/statm", pid);
    //puts(path);
    fp = fopen(path, "r");
    if (fp == NULL) {
        perror("/proc/%d/statm");
        return -1;
    }
    fscanf(fp, "%ld*", &vsize);
    fclose(fp);
    return vsize;
}

int main(int argc, char *argv[]) {
    if (argc != 2) {
        puts("arg err");
        return 0;
    }
    pid_t cpcld;
    int result = UN;
    cpcld = fork();
    if (cpcld == 0) {
        execlp("gcc", "gcc", argv[1], (char*)NULL);
        perror("excelp:");
        exit(0);
    }
    int stat;
    pid_t w;
    w = waitpid(cpcld, &stat, WUNTRACED);
    if (w == -1) {
        perror("waitpid");
        return 0;
    }
    if (WIFEXITED(stat)) {
        switch (WEXITSTATUS(stat)) {
        case EXIT_SUCCESS:
            puts("compile success");
            break;
        case 1:
            puts("compile error");
            exit(0);
        default:
            puts("????");
            exit(0);
        }
    } else {
        puts("compile is not exit success");
        exit(0);
    }
    pid_t runcld;
    runcld = fork();
    if (runcld == 0) {
        struct rlimit rlmt;
        rlmt.rlim_cur = rlmt.rlim_max = 1;
        if (0 != setrlimit(RLIMIT_CPU, &rlmt)) {
            perror("setrlimit");
        }

        rlmt.rlim_cur = rlmt.rlim_max = MEM_LMT;
        if (setrlimit(RLIMIT_AS, &rlmt) < 0) perror("setrlimit");

        rlmt.rlim_cur = rlmt.rlim_max = 6 * MB;
        if (setrlimit(RLIMIT_STACK, &rlmt) < 0) perror("setrlimit");

        rlmt.rlim_cur = rlmt.rlim_max = 2 * MB;
        if (setrlimit(RLIMIT_FSIZE, &rlmt) < 0) perror("setrlimit");

        ptrace(PTRACE_TRACEME, 0, NULL, NULL);
        //alarm(1);
        execl("./a.out", "a.out", (char*)NULL);
    }

    int t = 0;
    int time_usage = 0;
    long mem_usage = 0, now_mem;
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

        now_mem = get_mem_usage(runcld) * pagesize;
        if (mem_usage < now_mem) mem_usage = now_mem;
        if (mem_usage / MB + 3 > MEM_LMT / MB) {  //误差设置为3MB
            result = MLE;
            ptrace(PTRACE_KILL, runcld, NULL, NULL);
        }
    }

    if (result == RS) {

        result = AC;
    } else if (result == RK) {
        if (time_usage >= 990) {
            time_usage = 1000;
            result = TLE;
        } else if (result = RK) {
            mem_usage = MEM_LMT;
            result = MLE;
        }
    }

    printResult(result);
    printf("runtime: %dms\t mem_used: %ldKB\t\n", time_usage, mem_usage / KB + 1);
    return result;
}
