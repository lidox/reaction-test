#############################################
# Outlier Detection Algorithm Comparison    #
#############################################
library(plotly)
trace1 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("",  "5554096", "5651248", "5875992", "6265776", "6844528", "3531024", "4510928", "4075744"), 
  name = "Markov Inequality", 
  type = "bar", 
  uid = "1e"
)
trace2 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("", "279522", "372250", "483690", "605060", "521940", "380990", "437010", "442430"), 
  name = "Chebyshev Inequality", 
  type = "bar", 
  uid = "2e"
)
trace3 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("", "5079448", "5470961", "6884104", "6875352", "5102848", "7642808", "7837256", "4246888"), 
  name = "S_n Estimator", 
  type = "bar", 
  uid = "3e"
)
trace4 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("",  "4105296", "4227024", "4461712", "3094808", "3785800", "4722960", "3996000", "3778968"), 
  name = "Median Absolute Deviation", 
  type = "bar", 
  uid = "4e"
)

a <- c(as.numeric(trace1$y[2]),as.numeric(trace2$y[2]),as.numeric(trace3$y[2]),as.numeric(trace4$y[2]))
b <- c(as.numeric(trace1$y[3]),as.numeric(trace2$y[3]),as.numeric(trace3$y[3]),as.numeric(trace4$y[3]))
c <- c(as.numeric(trace1$y[4]),as.numeric(trace2$y[4]),as.numeric(trace3$y[4]),as.numeric(trace4$y[4]))
d <- c(as.numeric(trace1$y[5]),as.numeric(trace2$y[5]),as.numeric(trace3$y[5]),as.numeric(trace4$y[5]))
e <- c(as.numeric(trace1$y[6]),as.numeric(trace2$y[6]),as.numeric(trace3$y[6]),as.numeric(trace4$y[6]))
f <- c(as.numeric(trace1$y[7]),as.numeric(trace2$y[7]),as.numeric(trace3$y[7]),as.numeric(trace4$y[7]))
g <- c(as.numeric(trace1$y[8]),as.numeric(trace2$y[8]),as.numeric(trace3$y[8]),as.numeric(trace4$y[8]))


dat <- cbind(a,b,c,d, e, f, g)
barplot(dat,beside=TRUE, main="Algorithm Memory Comparison", xlab="RT data count"
        , ylab="Bytes", col=c("dodgerblue2","gold2","chartreuse3","deeppink"),
        ylim = c(0, 13190040), names.arg=c("1k","2k","4k", "6k", "8k","10k", "12k"))
legend("topright", 
       legend = c("Markov Inequality", "Chebyshev Inequality", "S_n Estimator", "Median Absolute Deviation"), 
       fill = c("dodgerblue2","gold2","chartreuse3","deeppink"))
