file_names = [
	'1375520740_Gobang_boardsize_6x4_numberOfGames_6144_iterStart_1100_iterEnd_3000_iterStep_100',
	'1375678015_Gobang_boardsize_6_numberOfGames_6144_iterStart_3500_iterEnd_6000_iterStep_500',
	'1375678130_Gobang_boardsize_6_numberOfGames_4096_iterStart_6500_iterEnd_10000_iterStep_500',
	'1375678169_Gobang_boardsize_6_numberOfGames_3072_iterStart_11000_iterEnd_15000_iterStep_1000',
	'1375678209_Gobang_boardsize_6_numberOfGames_3072_iterStart_16000_iterEnd_20000_iterStep_1000',
	'1375678261_Gobang_boardsize_6_numberOfGames_3072_iterStart_21000_iterEnd_25000_iterStep_1000'
]

x_data = []
y_data = []

files.each do |file_name|
	file_name = file_name + '/results.m'
	f = File.open(file_name, 'r')
	
	f.each do |line|
		if(line[0..7] == 'results')
			first_bracket = line.index('[')
			second_bracket = line.index(']')
			csv = line[first_bracket..second_bracket].split(',')
			sum = csv.inject{|sum,x| sum + x }
			total = csv.length
			y_data.append sum/total
		end
	end
end

f = File.open('output.m', 'w')
f << 'y_data = [' + y_data.join(",") + ']'