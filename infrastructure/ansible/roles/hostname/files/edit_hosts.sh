thishost=`hostname`

sed -i "/$thishost/d" /etc/hosts

echo "`ip addr | grep 'state UP' -A2 | tail -n1 | awk '{print $2}' | cut -f1  -d'/'` `hostname`" >> /etc/hosts
