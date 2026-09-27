GRADLE := ./gradlew
PASSWD := huber

# Update CATALINA_BASE if the Tomcat install moves. CONTEXT_NAME/WAR_NAME
# are fixed as `rentalapp` regardless of the project's version number in
# build.gradle, since bootWar's default output (rentalapp-<version>.war)
# would otherwise deploy under context /rentalapp-<version> instead of
# /rentalapp.
CATALINA_BASE := /opt/tomcat/9.0.113
CONTEXT_NAME  := rentalapp
WAR_NAME      := $(CONTEXT_NAME).war

.PHONY: all clean compile run wrapper pass war deploy redeploy undeploy

all: run

wrapper:
	gradle wrapper --gradle-version 8.7

pass:
	$(GRADLE) hashPassword -Ppassword='$(PASSWD)'

clean:
	$(GRADLE) clean

compile:
	$(GRADLE) compileJava compileGroovy

run:
	$(GRADLE) bootRun

# Builds the deployable WAR, then renames it to a fixed rentalapp.war —
# see CONTEXT_NAME/WAR_NAME above for why.
war:
	$(GRADLE) clean bootWar
	cp build/libs/rentalapp-*.war build/libs/$(WAR_NAME)

# Copies the freshly built WAR straight into Tomcat's webapps/ (relies on
# webapps/ being owned by the `tomcat` group with your user a member of it
# — see infra notes — so this needs no sudo).
#
# Deliberately does NOT stop/start Tomcat itself: tomcat-start/tomcat-stop
# are shell aliases from the srv/tomcat environment module, and aliases
# aren't reliably expanded in make's non-interactive recipe shell. Run
# those yourself, in your own shell, around this target — e.g.:
#   tomcat-stop && make deploy && tomcat-start
deploy: war
	cp build/libs/$(WAR_NAME) $(CATALINA_BASE)/webapps/$(WAR_NAME)

# Removes the deployed WAR and its exploded directory, so the next deploy
# starts from a clean slate instead of relying on Tomcat's
# auto-redeploy-on-timestamp-change behavior.
#
# The exploded directory needs sudo even though webapps/ itself is
# group-writable: Tomcat extracts a WAR's subdirectories (WEB-INF/,
# lib-provided/, etc.) as the `tomcat` user with the standard 755 mode —
# group gets read+execute, not write. The setgid bit on webapps/ only
# makes new entries inherit the tomcat *group*, not group-*writable*
# permission bits, so a mere fellow group member can't recursively delete
# what Tomcat itself created one level down.
undeploy:
	rm -f $(CATALINA_BASE)/webapps/$(WAR_NAME)
	sudo rm -rf $(CATALINA_BASE)/webapps/$(CONTEXT_NAME)

# Convenience: build + copy in one step. Same caveat as `deploy` above —
# still wrap this with tomcat-stop/tomcat-start yourself.
redeploy: deploy
