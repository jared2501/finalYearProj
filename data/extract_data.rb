file_names = [
	'1375520740_Gobang_boardsize_6x4_numberOfGames_6144_iterStart_1100_iterEnd_3000_iterStep_100',
	'1375678015_Gobang_boardsize_6_numberOfGames_6144_iterStart_3500_iterEnd_6000_iterStep_500',
	'1375678130_Gobang_boardsize_6_numberOfGames_4096_iterStart_6500_iterEnd_10000_iterStep_500',
	'1375678169_Gobang_boardsize_6_numberOfGames_3072_iterStart_11000_iterEnd_15000_iterStep_1000',
	'1375678209_Gobang_boardsize_6_numberOfGames_3072_iterStart_16000_iterEnd_20000_iterStep_1000',
	'1375678261_Gobang_boardsize_6_numberOfGames_3072_iterStart_21000_iterEnd_25000_iterStep_1000'
]

def gen_x_array(start,finish,i)
	(start..finish).step(i).to_a
end

def extract_range(string)
	iter_start_begin = string.index('iterStart_') + 10
	iter_start_finish = string.index('_iterEnd') - 1
	
	iter_end_begin = string.index('iterEnd_') + 8
	iter_end_finish = string.index('_iterStep') - 1
	
	iter_step_begin = string.index('iterStep_') + 9
	iter_step_finish = string.length - 1
	
	[string[iter_start_begin..iter_start_finish], string[iter_end_begin..iter_end_finish], string[iter_step_begin..iter_step_finish]].map{|x| x.to_i}
end

x_data = []
y_data = []


file_names.each do |file_name|
	x_data.push gen_x_array(*extract_range(file_name))
	
	file_name = file_name + '/results.m'
	f = File.open(file_name, 'r')
	
	f.each do |line|
		if(line[0...7] == 'results')
			first_bracket = line.index('[')
			second_bracket = line.index(']')
			csv = line[first_bracket..second_bracket].split(',').map{|x| x.to_f}
			sum = csv.inject(0) {|sum,x| sum + x }
			total = csv.length
			y_data.push(sum/total)
		end
	end
end

x_data.flatten(1)

f = File.open('output.m', 'w')
f.puts 'x_data = [' + x_data.join(",") + ']'
f.puts 'y_data = [' + y_data.join(",") + ']'


