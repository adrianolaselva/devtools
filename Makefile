.PHONY: help

CONTAINER_IMAGE_GROUP_NAME ?= "adrianolaselva"
CONTAINER_IMAGE_NAME ?= "devtools"
CONTAINER_IMAGE_CURRENT_VERSION ?= "latest"

include .settings
export $(shell sed 's/=.*//' .settings)

# Help: List All Commands❓
help:
	@(awk '/^#/{c=substr($$0,3);next}c&&/^[[:alpha:]][[:alnum:]-]+:/{print "\033[1;32m➜\033[0m \033[1m" substr($$1,1,index($$1,":")),c}1{c=0}' $(MAKEFILE_LIST) | column -s ":" -t)

# Application: Build Application Jar 📦
build-application-jar:
	@(mvn clean package -DskipTests)

# Application: Build Application Über Jar 📦
build-application-uber-jar:
	@(mvn clean package -Dquarkus.package.type=uber-jar -DskipTests)

# Application: Build Application Native Using GraalVM 📦
build-application-native:
	@(mvn package -Dnative -DskipTests)

# Application: Build Binary Linux 📦
build-package:
	@(./scripts/jpackage)

# Application: Build Docker Image 🐋
build-docker-image:
	@(mvn quarkus:image-build -DskipTests \
		  -Dquarkus.container-image.image=$(CONTAINER_IMAGE_NAME) \
		  -Dquarkus.container-image.group=$(CONTAINER_IMAGE_GROUP_NAME) \
		  -Dquarkus.container-image.tag=$(CONTAINER_IMAGE_CURRENT_VERSION))

# Application: Build Docker Image Native 🐋
build-docker-image-native:
	@(mvn quarkus:image-build -Dnative -DskipTests \
          -Dquarkus.container-image.image=$(CONTAINER_IMAGE_NAME) \
          -Dquarkus.container-image.group=$(CONTAINER_IMAGE_GROUP_NAME) \
          -Dquarkus.container-image.tag=$(CONTAINER_IMAGE_CURRENT_VERSION))

# Application: Run Unit Tests ▶️
run-unit-tests:
	@(mvn test -Dtest=*UnitTest)

# Application: Run Integration Tests ▶️
run-integrated-tests:
	@(mvn test -Dtest=*IntegrationTest)

# Application: Run Application ▶️
run:
	@(mvn clean compile assembly:single -DskipTests && java -jar target/devtools-*.jar $^ $@)
