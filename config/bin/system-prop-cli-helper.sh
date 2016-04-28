#!/bin/bash

INPUT=properties/dev.properties
OUTPUT_CLI=/tmp/jbosseap.cli
OUTPUT_REPLACE=/tmp/replace.txt
OUTPUT_SYS_PROP=/tmp/sysprop.txt

rm -f $OUTPUT_CLI

# Assist with creation of CLI system-property statements
while read prop_line; do 

    # skip comments and blank lines in prop file
    case "$prop_line" in \#*) continue ;; esac
    [ -z "$prop_line" ] && continue

    prop=${prop_line%%=*}
    prop_value=${prop_line#*=}
    echo -en "if (outcome == success) of /system-property=$prop:read-resource\n  /system-property=$prop:remove\nend-if\n" >> $OUTPUT_CLI
done <$INPUT

echo -en "\nbatch\n" >> $OUTPUT_CLI

while read prop_line; do

    # skip comments in prop file
    case "$prop_line" in \#*) continue ;; esac
    [ -z "$prop_line" ] && continue

    prop=${prop_line%%=*}
    echo -en "  /system-property=$prop:add(value=\"%$prop%\")\n" >> $OUTPUT_CLI
done <$INPUT

echo -en "run-batch" >> $OUTPUT_CLI

echo "Just wrote cli template to: $OUTPUT_CLI"

# Assist with creation of property replacement for:  com.google.code.maven-replacer-plugin
rm -f $OUTPUT_REPLACE
while read prop_line; do

    # skip comments and blank lines in prop file
    case "$prop_line" in \#*) continue ;; esac
    [ -z "$prop_line" ] && continue

    prop=${prop_line%%=*}
    echo -en "                  <replacement><token>%$prop%</token><value>\${$prop}</value></replacement>\n" >> $OUTPUT_REPLACE
done <$INPUT

echo "Just wrote replacer plugin template to: $OUTPUT_REPLACE"


# Assist with creation of property replacement for:  com.google.code.maven-replacer-plugin
rm -f $OUTPUT_SYS_PROP
while read prop_line; do

    # skip comments and blank lines in prop file
    case "$prop_line" in \#*) continue ;; esac
    [ -z "$prop_line" ] && continue

    prop=${prop_line%%=*}
    echo -en "                    <$prop>\${$prop}</$prop>\n" >> $OUTPUT_SYS_PROP
done <$INPUT

echo "Just wrote sys props  to: $OUTPUT_SYS_PROP"
