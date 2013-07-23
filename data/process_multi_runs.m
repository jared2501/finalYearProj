iterations = 5:5:395;
wins = sum(results,1)/1000;
plot(iterations,wins,iterations,0.5*ones(size(iterations)));