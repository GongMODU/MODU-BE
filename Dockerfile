FROM ubuntu:latest
LABEL authors="yuri"

ENTRYPOINT ["top", "-b"]