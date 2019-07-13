pushd Relocate
./gradlew --offline jar
popd
rm -rf out
rm -rf $FORGE/mcp/bin/minecraft/com/github/kb1000 || exit $?
mkdir out
javac -cp $FORGE/mcp/bin/minecraft:Relocate/download/build/libs/download.jar -d out `find src/main/java -regex ".*\\.java"` || exit $?
java -jar Relocate/build/libs/Relocate.jar -o relocated out Relocate/download/build/libs/download.jar || exit $?
java -cp Relocate/build/libs/Relocate.jar:relocated -Dretrolambda.outputDir=retrolambda -Dretrolambda.inputDir=relocated -Dretrolambda.classpath=$FORGE/mcp/bin/minecraft -Dretrolambda.defaultMethods=true -Dretrolambda.bytecodeVersion=50 net.orfjackal.retrolambda.Main || exit $?
jar cvf DiscordChat.jar -C retrolambda . || exit $?
cp -r retrolambda/* $FORGE/mcp/bin/minecraft/ || exit $?
pushd $FORGE/mcp
./reobfuscate_srg.sh || exit $?
popd
jar cvf DiscordChat-obf.jar -C $FORGE/mcp/reobf/minecraft . || exit $?
rm -rf $FORGE/mcp/bin/minecraft/com/github/kb1000 || exit $?
