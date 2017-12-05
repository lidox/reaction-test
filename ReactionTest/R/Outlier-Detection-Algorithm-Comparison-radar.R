#############################################
# Outlier Detection Algorithm Skills    #
#############################################
library(fmsb)

data=as.data.frame(matrix( c(10, 44, 77, 32) , ncol=4))
colnames(data)=c("accurancy" , "memory" , "speed" , "easy" )

# To use the fmsb package, add 2 lines to the dataframe: 
# the max and min of each topic to show on the plot!
data=rbind(rep(100,4) , rep(0,4) , data)

data
# The default radar chart proposed by the library:
radarchart(data)

# Custom the radarChart !
radarchart( data  , axistype=1 , 
            
            #custom polygon
            pcol=rgb(0.2,0.5,0.5,0.9) , pfcol=rgb(0.2,0.5,0.5,0.5) , plwd= 4 , 
            
            #custom the grid
            cglcol="grey", cglty=1, axislabcol="grey", caxislabels=seq(0,100,25), cglwd=0.8,
            
            #custom labels
            vlcex=0.8 
)

