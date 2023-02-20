#!/bin/bash
## -XX:CRaCCheckpointTo=/cracdata  

if [ -f /cracdata/files.img ]
then 
	/usr/local/openjdk-17/bin/java -XX:-UsePerfData  -XX:+UseSerialGC   -XX:CRaCRestoreFrom=/cracdata -Xshare:on    -Djava.awt.headless=true    -Djava.library.path=/function/runtime/lib    -cp    /function/app/*:/function/runtime/*:/function/app:/function/app/resources    com.fnproject.fn.runtime.EntryPoint    $@ >> /cracdata/log 2>&1
	ps ax >> /cracdata/log
else 
	ps ax >> /cracdata/log
	ps ax >> /cracdata/log
	/usr/local/openjdk-17/bin/java -XX:-UsePerfData  -XX:+UseSerialGC   -XX:CRaCCheckpointTo=/cracdata -Xshare:on    -Djava.awt.headless=true    -Djava.library.path=/function/runtime/lib    -cp    /function/app/*:/function/runtime/*:/function/app:/function/app/resources    com.fnproject.fn.runtime.EntryPoint    $@   >> /cracdata/log 2>&1
	if [ -f /cracdata/files.img ]
	then
		ps ax >> /cracdata/log
		ps ax >> /cracdata/log
		/usr/local/openjdk-17/bin/java -XX:-UsePerfData  -XX:+UseSerialGC   -XX:CRaCRestoreFrom=/cracdata -Xshare:on    -Djava.awt.headless=true    -Djava.library.path=/function/runtime/lib    -cp    /function/app/*:/function/runtime/*:/function/app:/function/app/resources    com.fnproject.fn.runtime.EntryPoint    $@ >> /cracdata/log 2>&1
	fi
fi
