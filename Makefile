build:
	@./gradlew build

test:
	@./gradlew test

test-coverage-verification: test
	@./gradlew jacocoTestCoverageVerification

spotbugs:
	@./gradlew spotbugsMain spotbugsTest spotbugsIntegrationTest

checkstyle:
	@./gradlew checkstyleMain checkstyleTest checkstyleIntegrationTest

run: build
	@./gradlew bootRun --args='--spring.profiles.active=local'

clean:
	@./gradlew clean

version:
	@./gradlew --version

upgrade:
	@if [ -z "$(GRADLE_VERSION)" ]; then \
		echo "Warning: GRADLE_VERSION is not set. Usage: make upgrade GRADLE_VERSION=X.Y.Z"; \
	else \
		./gradlew wrapper --gradle-version $(GRADLE_VERSION) --distribution-type all; \
	fi
