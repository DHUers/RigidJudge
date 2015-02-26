[![Build Status](https://travis-ci.org/DHUers/RigidJudge.svg?branch=master)](https://travis-ci.org/DHUers/RigidJudge)

# RigidJudge
A powerful and stable judge system designed for RigidOJ.

It can also be used by other OnlineJudge system which takes RabbitMQ as the distribution intermediary.

## Features
### Local Judge
* Sandbox protection (Linux)
* SpecialJudge (Program comparison)
* Multiple test case judge
* Time & memory limits for different languages
* Problem data download & cache
* Generate Diff report (differences between output and standard answer)
* Language & Compiler Support:

    GCC G++ JDK Clang

### Remote Judge
* Proxy to remote OnlineJudges
* Concurrent tasks
* Automatically retry
* Remote OnlineJudge Support:

    UVa POJ HDU ZOJ UVaLive SGU SPOJ AIZU Ural Codeforces Codeforces::Gym

## Requirements
* JRE 7
* RabbitMQ 3.4

## Support

Visit the [GitHub Issues][1].


[1]: https://github.com/DHUers/RigidJudge/issues
