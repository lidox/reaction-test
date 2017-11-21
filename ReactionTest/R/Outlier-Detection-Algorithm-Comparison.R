#############################################
# Outlier Detection Algorithm Comparison    #
#############################################
library(plotly)
trace1 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("", "407490", "165340", "291240", "264440", "312320", "315630", "349670", "313830"), 
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
  y = c("", "7624890", "82348580", "761216120", "2106267720", "4153665980", "6606401450", "9864644340", "14190604980"), 
  name = "S_n Estimator", 
  type = "bar", 
  uid = "3e"
)
trace4 <- list(
  x = c("", "100", "1000", "2000", "4000", "6000", "8000", "10000", "12000"), 
  y = c("",  "430080", "864980", "1373970", "473450", "593010", "636390", "668620", "1050500"), 
  name = "Median Absolute Deviation", 
  type = "bar", 
  uid = "4e"
)
data <- list(trace1, trace2, trace3, trace4)
layout <- list(
  autosize = TRUE, 
  title = "Algorithm Speed Comparison", 
  xaxis = list(
    autorange = TRUE, 
    range = c(0.5, 13), 
    title = "Data size", 
    type = "linear"
  ), 
  yaxis = list(
    autorange = TRUE, 
    range = c(1, 15190604980), 
    title = "Time in milli seconds"
  )
)

p <- plot_ly()
p <- add_trace(p, x=trace1$x, y=trace1$y, name=trace1$name, type=trace1$type, uid=trace1$uid)
p <- add_trace(p, x=trace2$x, y=trace2$y, name=trace2$name, type=trace2$type, uid=trace2$uid)
p <- add_trace(p, x=trace3$x, y=trace3$y, name=trace3$name, type=trace3$type, uid=trace3$uid)
p <- add_trace(p, x=trace4$x, y=trace4$y, name=trace4$name, type=trace4$type, uid=trace4$uid)
p <- layout(p, autosize=layout$autosize, height=layout$height, title=layout$title, width=layout$width, xaxis=layout$xaxis, yaxis=layout$yaxis)
p

#########################################################################
#########################################################################

# Grouped Bar Plot
counts <- c(trace1$y, trace2$y)
barplot(counts, main="Car Distribution by Gears and VS",
        xlab="Number of Gears", col=c("darkblue","red"),
        legend = rownames(counts), beside=TRUE)


a <- c(as.numeric(trace1$y[2]),as.numeric(trace2$y[2]),as.numeric(trace3$y[2]),as.numeric(trace4$y[2]))
b <- c(as.numeric(trace1$y[3]),as.numeric(trace2$y[3]),as.numeric(trace3$y[3]),as.numeric(trace4$y[3]))
c <- c(as.numeric(trace1$y[4]),as.numeric(trace2$y[4]),as.numeric(trace3$y[4]),as.numeric(trace4$y[4]))
d <- c(as.numeric(trace1$y[5]),as.numeric(trace2$y[5]),as.numeric(trace3$y[5]),as.numeric(trace4$y[5]))
e <- c(as.numeric(trace1$y[6]),as.numeric(trace2$y[6]),as.numeric(trace3$y[6]),as.numeric(trace4$y[6]))
f <- c(as.numeric(trace1$y[7]),as.numeric(trace2$y[7]),as.numeric(trace3$y[7]),as.numeric(trace4$y[7]))
g <- c(as.numeric(trace1$y[8]),as.numeric(trace2$y[8]),as.numeric(trace3$y[8]),as.numeric(trace4$y[8]))


dat <- cbind(a,b,c,d, e, f, g)
barplot(dat,beside=TRUE, main="Algorithm Speed Comparison", xlab="RT data count"
        , ylab="Time in nano seconds", col=c("dodgerblue2","gold2","chartreuse3","deeppink"),
        ylim = c(0, 13190604980), names.arg=c("1k","2k","4k", "6k", "8k","10k", "12k"))
legend("topleft", 
       legend = c("Markov Inequality", "Chebyshev Inequality", "S_n Estimator", "Median Absolute Deviation"), 
       fill = c("dodgerblue2","gold2","chartreuse3","deeppink"))



#########################################################################
#########################################################################

# Grouped Bar Plot
counts <- c(trace1$y, trace2$y)
barplot(counts, main="Car Distribution by Gears and VS",
        xlab="Number of Gears", col=c("darkblue","red"),
        legend = rownames(counts), beside=TRUE)


a <- c(as.numeric(trace1$y[2]),as.numeric(trace2$y[2]),as.numeric(trace4$y[2]))
b <- c(as.numeric(trace1$y[3]),as.numeric(trace2$y[3]),as.numeric(trace4$y[3]))
c <- c(as.numeric(trace1$y[4]),as.numeric(trace2$y[4]),as.numeric(trace4$y[4]))
d <- c(as.numeric(trace1$y[5]),as.numeric(trace2$y[5]),as.numeric(trace4$y[5]))
e <- c(as.numeric(trace1$y[6]),as.numeric(trace2$y[6]),as.numeric(trace4$y[6]))
f <- c(as.numeric(trace1$y[7]),as.numeric(trace2$y[7]),as.numeric(trace4$y[7]))
g <- c(as.numeric(trace1$y[8]),as.numeric(trace2$y[8]),as.numeric(trace4$y[8]))


dat <- cbind(a,b,c,d, e, f, g)
colors <- c("dodgerblue2","gold2","chartreuse3")
barplot(dat,beside=TRUE, main="Algorithm Speed Comparison", xlab="RT data count"
        , ylab="Time in nano seconds", col=colors,
        ylim = c(0, 1590490), names.arg=c("1k","2k","4k", "6k", "8k","10k", "12k"),
        legend.text = rownames(c("a","b","c")))
legend("topright", 
       legend = c("Markov Inequality", "Chebyshev Inequality", "Median Absolute Deviation"), 
       fill = colors)



#########################################################################
#########################################################################

# install.packages('plotrix', dependencies = TRUE)
require(plotrix)

d = t(matrix( c(407490, 165340, 291240, 264440, 312320, 315630, 349670, 313830, 
                2086550, 311720, 412920, 657470, 504770, 499950, 439120, 390630,
                7624890, 82348580, 761216120, 2106267720, 4153665980, 6606401450, 9864644340, 14190604980,
                468630, 730950, 2451880, 437910, 593610, 594520, 778840, 680050),nrow=8, ncol=4 ))

colnames(d)=c("100", "1k", "2k", "4k", "6k", "8k", "10k", "12k")

# add row of NAs for spacing
d=rbind(NA,d)
d

# create barplot and store returned value in 'a'
a = gap.barplot(as.matrix(d), 
                gap=c(8624890,10624890), 
                ytics=c(0,100000,465340,800000,1000000,2186550,15190604980),
                xaxt='n') # disable the default x-axis

# calculate mean x-position for each group, omitting the first row 
# first row (NAs) is only there for spacing between groups
aa = matrix(a, nrow=nrow(d))
xticks = colMeans(aa[2:nrow(d),])

# add axis labels at mean position
axis(1, at=xticks, lab=LETTERS[1:8])

#########################################################
#########################################################

# install.packages('plotrix', dependencies = TRUE)
require(plotrix)

d = t(matrix( c(7,3,2,3,2,2,852,268,128,150,
                127,74,5140,1681,860,963,866,
                470,26419,8795,4521,5375,4514,2487),
              nrow=6, ncol=4 ))

# Hack for grouping (leaves the extra space at the end)
e = as.vector(rbind(d, rep(NA, 6)))[1:29]

a = gap.barplot(ceiling(as.matrix(e/60)), 
                gap=c(160,390),
                col=rep(c(grey.colors(4), 1), 6),
                #space=rep(c(rep(0,3), 1), 6),
                ytics=c(0,50,100,150,400,420,440),
                xaxt='n') # disable the default x-axis

xticks=c(2.5, 7.5, 12.5, 17.5, 22.5, 27.5)

# add axis labels at mean position
axis(1, at=xticks, LETTERS[1:6] )

legend("topright", LETTERS[7:10],
       bty="n",  
       fill=grey.colors(4)) 

####################################
####################################

#########################################################
#########################################################

# install.packages('plotrix', dependencies = TRUE)
require(plotrix)

d = t(matrix( c(7,3,2,3,2,2,852,268,128,150,
                127,74,5140,1681,860,963,866,
                470,26419,8795,4521,5375,4514,2487),
              nrow=6, ncol=4 ))

# Hack for grouping (leaves the extra space at the end)
e = as.vector(rbind(d, rep(NA, 6)))[1:29]

a = gap.barplot(ceiling(as.matrix(e/60)), 
                gap=c(160,390),
                gap=c(400,410),
                col=rep(c(grey.colors(4), 1), 6),
                #space=rep(c(rep(0,3), 1), 6),
                ytics=c(0,50,100,150,400,420,440),
                xaxt='n') # disable the default x-axis

xticks=c(2.5, 7.5, 12.5, 17.5, 22.5, 27.5)

# add axis labels at mean position
axis(1, at=xticks, LETTERS[1:6] )

legend("topright", LETTERS[7:10],
       bty="n",  
       fill=grey.colors(4)) 

