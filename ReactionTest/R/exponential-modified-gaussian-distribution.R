library(emg)

primary_cols <- list(green= "#5FF847",turkis= "#35CEC7",blue= "#6A95FB",pink= "#C63AA0",orange= "#FF8C09",fancy_pink= "#FF0961",dark_blue= "#032F5E")

plot(demg, -2, 8, col=primary_cols$fancy_pink, ylab = "frequency", xlab = "data", main = "Exponential modified gaussian distribution")
