preopdata <- c(375,331,290,343,313,361,340,1395,476,394)
intraopdata <- c(628, 795,396,403,576,676,2336,481,2048,389,428,614,761,347,355)
plot(preopdata,type="l",col="blue",lwd=2,xlab="time",ylab="reaction time in ms",main="Reaction Times",xlim=c(0,15),ylim=c(0,2500))
lines(intraopdata,col="green",lwd=1)

prein <- rbind(intraopdata, preopdata)
hist(prein,breaks=10)
