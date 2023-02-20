FROM registry.access.redhat.com/ubi8/ubi:8.7
RUN  dnf -y install  wget procps-ng criu gzip   tar   binutils   freetype fontconfig  ;  dnf clean all # buildkit
ENV JAVA_HOME=/usr/local/openjdk-17
ENV PATH=/usr/local/openjdk-17/bin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin
ENV LANG=C.UTF-8 
ENV JAVA_VERSION=17.0.2
COPY lib/libfnunixsocket.so /function/runtime/lib/ 
COPY runtime/*.jar /function/runtime/ 
#ADD openjdk-17.tgz /usr/local
RUN wget -O jdk.tgz --quiet https://github.com/CRaC/openjdk-builds/releases/download/17-crac%2B4/openjdk-17-crac+4_linux-x64.tar.gz && tar zxf jdk.tgz && mv openjdk-17-crac+4_linux-x64 /usr/local/openjdk-17 && rm jdk.tgz
RUN /usr/local/openjdk-17/bin/java -Xshare:dump 
RUN groupadd --system --gid 1000 fn ; adduser --uid 1000 --gid 1000 fn 
COPY entrypoint.sh /
ENTRYPOINT ["/entrypoint.sh"]
#ENTRYPOINT ["/usr/local/openjdk-17/bin/java", "-XX:-UsePerfData",    "-XX:+UseSerialGC",    "-Xshare:on",   "-Djava.awt.headless=true",    "-Djava.library.path=/function/runtime/lib",    "-cp",    "/function/app/*:/function/runtime/*:/function/app:/function/app/resources",    "com.fnproject.fn.runtime.EntryPoint"]
WORKDIR /function
#CMD ["com.example.fn.HelloFunction::handleRequest"]
