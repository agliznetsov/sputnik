#!/usr/bin/env bash
#
# Startup script for sputnik under *nix systems.


##################################################
# Set the name which is used by other variables.
# Defaults to the file name without extension.
##################################################
NAME=$(echo $(basename $0) | sed -e 's/^[SK][0-9]*//' -e 's/\.sh$//')


# To get the service to restart correctly on reboot, uncomment below:
# ========================
# chkconfig: 3 99 99
# ========================

usage()
{
    echo "Usage: ${0##*/} [-d] {start|stop|restart|run|status}"
    exit 1
}

[ $# -gt 0 ] || usage


##################################################
# Some utility functions
##################################################

findDirectory()
{
  local L OP=$1
  shift
  for L in "$@"; do
    [ "$OP" "$L" ] || continue
    printf %s "$L"
    break
  done
}

findJar()
{
  for f in "$1/*.jar"; do
    printf %s "$f"
    break
  done
}

running()
{
  if [ -f "$1" ]
  then
    local PID=$(cat "$1" 2>/dev/null) || return 1
    kill -0 "$PID" 2>/dev/null
    return
  fi
  rm -f "$1"
  return 1
}


##################################################
# Get the action & configs
##################################################

while [[ $1 = -* ]]; do
  case $1 in
    -d) DEBUG=1 ;;
  esac
  shift
done
ACTION=$1
shift


if [ -z "$SPUTNIK_HOME" ]; then
  SPUTNIK_HOME=/opt/sputnik
fi

if [ -z "$SPUTNIK_BASE" ]; then
  SPUTNIK_BASE=/var/sputnik
fi


#####################################################
# Find a location for the pid file
#####################################################
SPUTNIK_RUN=$(findDirectory -w /var/run $SPUTNIK_BASE)

#####################################################
# Find a pid and state file
#####################################################
SPUTNIK_PID="$SPUTNIK_RUN/${NAME}.pid"
SPUTNIK_JAR=$(findJar "$SPUTNIK_HOME/bin")
JVM_ARGS=(-Xms128m -Xmx256m)
RUN_ARGS=(-Dlogging.config="$SPUTNIK_BASE/config/logback.xml" -jar "$SPUTNIK_JAR")
RUN_CMD=("java" ${JVM_ARGS[@]} ${RUN_ARGS[@]})

##################################################
# Do the action
##################################################
case "$ACTION" in
  start)
    echo -n "Starting Sputnik: "

      if running $SPUTNIK_PID
      then
        echo "Already running $(cat $SPUTNIK_PID)!"
        exit 1
      fi

      cd "$SPUTNIK_BASE" 
      "${RUN_CMD[@]}" > /dev/null &
      disown $!
      echo $! > "$SPUTNIK_PID"
      echo OK

    ;;

  stop)
    echo -n "Stopping Sputnik: "
      if [ ! -f "$SPUTNIK_PID" ] ; then
        echo "ERROR: no pid found at $SPUTNIK_PID"
        exit 1
      fi

      PID=$(cat "$SPUTNIK_PID" 2>/dev/null)
      if [ -z "$PID" ] ; then
        echo "ERROR: no pid id found in $SPUTNIK_PID"
        exit 1
      fi
      kill "$PID" 2>/dev/null

      TIMEOUT=5
      while running $SPUTNIK_PID; do
        if (( TIMEOUT-- == 0 )); then
          kill -KILL "$PID" 2>/dev/null
        fi
        sleep 1
      done

    rm -f "$SPUTNIK_PID"
    echo OK

    ;;

  restart)
    SPUTNIK_SH=$0
    if [ ! -f $SPUTNIK_SH ]; then
      SPUTNIK_SH=$SPUTNIK_HOME/bin/sputnik.sh
    fi

    "$SPUTNIK_SH" stop "$@"
    "$SPUTNIK_SH" start "$@"

    ;;

  run)
    echo "Running Sputnik: "

    if running "$JETTY_PID"
    then
      echo Already running $(cat "$JETTY_PID")!
      exit 1
    fi

    cd "$SPUTNIK_BASE"
    exec "${RUN_CMD[@]}"
    ;;

  status)
    echo "RUN_CMD =  ${RUN_CMD[*]}"


    if running "$SPUTNIK_PID"
    then
      echo "Sputnik is running pid=$(< "$SPUTNIK_PID")"
    else
      echo "Sputnik is NOT running"
    fi

    if running "$SPUTNIK_PID"
    then
      exit 0
    fi
    exit 1

    ;;

  *)
    usage

    ;;
esac

exit 0
