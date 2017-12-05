###########################################
# HISTOGRAM CLEAN INTRA-OP REACTION TIMES #
###########################################

# read data from CSV
data <- read.csv("/home/georgo/Schreibtisch/test.csv", header=FALSE)

# show summary
summary(data)

# aggregate reaction times
data <- c(data[data$V6 > 0 & data$V5 == "InOperation",], data$V7, data$V8, data$V9, data$V10)

# remove NA
data <- data[!is.na(data)]

# remove outliers
data <- data[data < 5000]

# difine breaks
br <- seq(0, 10000, by = 50)

# plot histogram
hist(data, xlab = "RT in ms", main = "Reaction Time Data", xlim=c(0,2000), breaks= br)

summary(data)
