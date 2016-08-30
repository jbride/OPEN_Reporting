# SETUP
# local workstation:
#   yum install 
# remote environments (dev and prod) :
#   yum install -y MySQL-python.x86_64


# sanity check
# ansible mysqldev -m ping -i . -vvv -u root
# ansible mysqlprod -m ping -i . -vvv -u jbride-redhat.com

# to test only the notification
# ansible-playbook -i hosts mysql_playbook.yml --tags notification 

# execution
# NOTE:  execute the following from your local workstation
# public key auth needs to be set up beforehand from your local workstation to both prod and dev environments
# dump prod database, zip and download to local workstation :           ansible-playbook -i hosts mysql_playbook.yml --tags dump
# upload zip to dev and import into mysql in dev            :           ansible-playbook -i hosts mysql_playbook.yml --tags import
