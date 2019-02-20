FROM sonatype/nexus:2.14.11-01

USER root

RUN yum -y update; yum clean all
RUN yum install -y git; yum clean all

USER nexus

RUN mkdir /tmp/build