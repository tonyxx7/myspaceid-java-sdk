rem To run this, use runtest 2> outputfile
java -classpath "lib/json_simple-1.1.jar;build" -Dhttp.proxyHost=localhost -Dhttp.proxyPort=8888 com.myspace.myspaceid.PortableContactsTest %1 %2 %3 %4 %5 %6
java -classpath "lib/json_simple-1.1.jar;build" -Dhttp.proxyHost=localhost -Dhttp.proxyPort=8888 com.myspace.myspaceid.RestV1Test %1 %2 %3 %4 %5 %6
rem java -classpath "lib/json_simple-1.1.jar;build" -Dhttp.proxyHost=localhost -Dhttp.proxyPort=8888 com.myspace.myspaceid.MySpaceTest %1 %2 %3 %4 %5 %6
rem java -classpath "lib/json_simple-1.1.jar;build" com.myspace.myspaceid.MySpaceTest %1 %2 %3 %4 %5 %6
