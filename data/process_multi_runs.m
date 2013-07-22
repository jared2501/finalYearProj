iterations = 100:200:5000;
wins = sum(results,1)/1000;
plot(iterations,wins,iterations,0.5*ones(size(iterations)));