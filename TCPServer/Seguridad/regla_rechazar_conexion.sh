#!/bin/bash
#
# Settings
#
IPT=/sbin/iptables
# Max numero de conexiones en segs
SECONDS=1
# Max conexiones por IP
BLOCKCOUNT=3
# ....
# ..
# La accion por default puede ser DROP o REJECT
DACTION="REJECT"
$IPT -A INPUT -p tcp --dport 8080  -m state --state NEW -m recent --set
$IPT -A INPUT -p tcp --dport 8080  -m state --state NEW -m recent --update --seconds ${SECONDS} --hitcount ${BLOCKCOUNT} -j ${DACTION}