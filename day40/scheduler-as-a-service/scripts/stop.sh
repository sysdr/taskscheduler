#!/bin/bash

echo "Stopping all services..."

if [ -f scheduler.pid ]; then
    kill $(cat scheduler.pid)
    rm scheduler.pid
fi

if [ -f payment.pid ]; then
    kill $(cat payment.pid)
    rm payment.pid
fi

if [ -f notification.pid ]; then
    kill $(cat notification.pid)
    rm notification.pid
fi

if [ -f dashboard.pid ]; then
    kill $(cat dashboard.pid)
    rm dashboard.pid
fi

echo "All services stopped!"
