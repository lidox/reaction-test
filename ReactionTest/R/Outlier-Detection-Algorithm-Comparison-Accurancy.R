myxlabels = c(538, 762 , 877 , 1000, 1200, 1250, 1300, 2000, 3000, 4805)

#####################################################
cars <- c(0.14111379354159517, 0.19986749196783557, 0.2300312210705929, 0.262293296545716, 0.31475195585485916, 0.32786662068214495, 0.34098128550943074, 0.524586593091432, 0.7868798896371478, 1.2603192899021651)

# Graph cars using blue points overlayed by a line 
plot(cars, type="o", col="dodgerblue2", xlab="Observation to check", ylab="Score", xaxt = "n")

# Create a title with a red, bold/italic font
title(main="Markov Inequality")
abline(h=1, col="red")
axis(1, at=1:10, labels=myxlabels)
#####################################################
cars <- c(0.11146327126336888, 2.508189883849111E-4, 0.05684485963493275, 0.11791241155378555, 0.2172092439421641, 0.24203345203925875, 0.26685766013635337, 0.6143965734956782, 1.110880735437571, 2.0070346477426875)

# Graph cars using blue points overlayed by a line 
plot(cars, type="o", col="dodgerblue2", xlab="Observation to check", ylab="Score", xaxt = "n")

# Create a title with a red, bold/italic font
title(main="Chebyshev Inequality")
abline(h=1, col="red")
axis(1, at=1:10, labels=myxlabels)
#####################################################
cars <- c(0.0, 2.07564, 3.1412587499999995, 4.2810075, 6.1342575, 6.59757, 7.0608825, 13.547257499999999, 22.813507499999996, 39.53908875)

# Graph cars using blue points overlayed by a line 
plot(cars, type="o", col="dodgerblue2", xlab="Observation to check", ylab="Score", xaxt = "n")

# Create a title with a red, bold/italic font
title(main="MAD")
abline(h=3, col="red")
axis(1, at=1:10, labels=myxlabels)
#####################################################
cars <- c(0.6632123146471394, 1.3014762036649192, 1.6923348718582176, 2.1663549588160476, 2.937677117857078, 3.133106451953727, 3.33685192792683, 6.116522350131298, 10.237170649913837, 17.742488693412813)

# Graph cars using blue points overlayed by a line 
plot(cars, type="o", col="dodgerblue2", xlab="Observation to check", ylab="Score", xaxt = "n")

# Create a title with a red, bold/italic font
title(main="S_n Estimator")
abline(h=3, col="red")
axis(1, at=1:10, labels=myxlabels)

median = 538.0
mean = 762.5051903114168
min = 214.0
max = 4805.0
sdtdev = 636.934247368496