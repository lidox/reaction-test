################################
# REACTION TIMES CONVERTER     #
################################

# read data from CSV
data <- read.csv("/home/georgo/Schreibtisch/reaction-data26-07-2017.csv", header=FALSE)

# comma am ende => zeilenweise prüfen
# [zeilen,spalten]
# [zelle]
data <- data[data$V1!='sabel',]
data <- data[data$V1!='test',]
data <- data[data$V1!='Demo',]

# blacking out the names
data$V1 <- abbreviate(data$V1)

# show summary
summary(data) 

# select colums to deal with
tmp <- data[,c('V7','V8','V9','V10','V11')]

# select data points to filter
mask <- tmp > 5000 &! is.na(tmp)

# just for debugging
tmp[mask]

# edit selected values
new_vals <- data[,c('V7','V8','V9','V10','V11')][mask]/100

# overwrite selected values
data[,c('V7','V8','V9','V10','V11')][mask] <- new_vals

#check result: how much elements are bigger than 5000
sum(data[,c('V7','V8','V9','V10','V11')] > 5000, na.rm=TRUE)

# remove trials and postOperativ
data <- data[!(data$V5=='Trial' | data$V5=='PostOperation'),]
# alternative
# data <- subset(data, !(data$V5=='Trial' | data$V5=='PostOperation'))

# check data
sum(data[,c('V5')] =='PostOperation')

######################
# Write to csv-file  #
######################
write.csv(data, file = "/home/georgo/Schreibtisch/reaction-data-july.csv", row.names = FALSE)

# make hist
data <- c(data$V11, data$V7, data$V8, data$V9, data$V10)
br <- seq(0, 10000, by = 50)

# plot histogram
hist(data, xlab = "RT in ms", main = "Reaction Time Data", xlim=c(0,2000), breaks= br)
ggplot(data)

