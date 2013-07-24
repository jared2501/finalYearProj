iterations = 1:14;
wins = sum(results,1)/1024;
plot(iterations,wins,iterations,0.5*ones(size(iterations)));