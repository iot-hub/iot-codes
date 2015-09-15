#!/bin/bash
# -------------------------------------------------------

parm1=$1
parm1=${parm1//[\/]/" "}  # replace the / with a space
identity=( $parm1 )
userid=${identity[0]}
device=${identity[1]}

case $2 in
 "SOUND") 
    case $3 in
        1)  aplay bell.wav
        ;;
        2)  aplay bell2.wav
        ;;
        3)  aplay honk.wav
        ;;
        *)  aplay bell.wav
        ;;
    esac
    echo "played"
    ;;
 "PULL") 
    sqlite3 IOTdb 'select datavalue from IOTdata;' > sqlout.tmp
    cat sqlout.tmp
    ;;
 "PUSH") 
    event=$(date +'%Y-%m-%d:%H:%M:%S')
    value=$3
    sqlite3 IOTdb 'insert into IOTdata values("'$event'","'$userid'","'$device'",'$value');'
    echo $3
    ;;
 *) echo $userid"-"$2$3$4$5$6
esac




