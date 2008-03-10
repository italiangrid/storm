#!/bin/sh

RETVAL=0

umask 077
host=127.0.0.1
port=4444

start() {
        echo -n $"Starting StoRM server..."
	echo ""
	./start >>/dev/null
	echo -n $"StoRM server started."
	echo ""
	return $RETVAL
}

stop() {
        echo -n $"Stopping StoRM server..."
	echo ""
        ./stop >>/dev/null
        echo -n $"StoRM server stopped."
	echo ""
        return $RETVAL
}

shutdown() {
        echo -n $"Shutting down StoRM server..."
	echo ""
        ./shutdown >>/dev/null
        echo -n $"StoRM server off."
	echo ""
        return $RETVAL
}
case "$1" in
  start)
        start
        ;;
  stop)
        stop
        ;;
  shutdown)
        shutdown
        ;;
  *)
        echo $"Usage: $0 {start|stop|shutdown}"
        exit 1
esac

exit $?
