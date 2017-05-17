binary <- read.csv("nnetResultsOutput_binary.csv", header = F)
binary.epoch <- read.csv("optimalEpoch.csv", header = F)
mu <- mean(binary.epoch$V2)
sdev <- sd(binary.epoch$V2)

x <- data.frame(iteration = rep(1:50, each = 10000), binary) 

require(ggplot2)
require(plyr)
require(dplyr)
	p <- x %>% 
		mutate(iteration = as.factor(iteration)) %>%
		group_by(iteration) %>%
		filter(V1 < 10000) %>%
		ggplot(aes(y = V2, x = V1, colour = iteration)) +
		xlab("Epochs") + ylab("TSE") +
		geom_line() +
		theme_bw() +
		theme(legend.position="none") +
		geom_vline(xintercept = mu, size = 1.5) +
		geom_vline(xintercept = mu+sdev, colour = "red", linetype = "dashed", size = 1.5) +
		geom_vline(xintercept = mu-sdev, colour = "red", linetype = "dashed", size = 1.5)
	
