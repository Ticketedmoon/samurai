# Disable memory paging and swapping performance on the host to improve performance.
sudo swapoff -a

# Increase the number of memory maps available to OpenSearch.

## Edit the sysctl config file
sudo vi /etc/sysctl.conf

## Add a line to define the desired value
## or change the value if the key exists,
## and then save your changes.
vm.max_map_count=262144

## Reload the kernel parameters using sysctl
sudo sysctl -p

## Verify that the change was applied by checking the value
cat /proc/sys/vm/max_map_count
