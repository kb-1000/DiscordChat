rm -rf out
mkdir out
javac -cp $FORGE/mcp/bin/minecraft:Relocate/download/build/libs/download.jar -target 1.6 -source 1.6 -d out `find src/main/java -regex ".*\\.java"`
java -jar Relocate/build/libs/Relocate.jar -o relocated out Relocate/download/build/libs/download.jar
jar cvf DiscordChat.jar -C relocated .
cp -r relocated/* $FORGE/mcp/bin/minecraft/
pushd $FORGE/mcp
./reobfuscate_srg.sh
popd
jar cvf DiscordChat-obf.jar -C $FORGE/mcp/reobf/minecraft .
