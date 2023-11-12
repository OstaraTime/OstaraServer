PROJECT=ostara
MAINCLASS=${PROJECT}/Main
OUTJAR=Ostara.jar

default:
	javac -Xlint ${MAINCLASS}.java -d build -cp build:.
run:
	cd build && java ${MAINCLASS}
clean:
	rm -rf build/${PROJECT}/*
leakproof:
	cd build && java -Xmx64M ${MAINCLASS}
buildjar:
	cd build && jar cfM ${OUTJAR} com org ostara META-INF
install:
	bash install
