cd ~/IdeaProjects/SOVaadinSvg
mvn clean install -Pdirectory
mkdir -p zipTarget
rm -f zipTarget/*.zip
cp target/*.zip zipTarget
