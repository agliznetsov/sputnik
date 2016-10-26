#!/usr/bin/env bash
free | awk '{
   if ($1 ~ "^Mem:"){
     printf("mem.total=%d\nmem.used=%d\n",$2,$3)
   }
   if ($1 ~ "^Swap:"){
     printf("swap.total=%d\nswap.used=%d\n",$2,$3)
   }
}'
uptime | awk '{ for(i=1;i<=NF;i++){ if($i=="average:"){tmp = $(i+1); v = substr(tmp, 0, length(tmp)-1); printf("load.average=%s\n", v)} } }'