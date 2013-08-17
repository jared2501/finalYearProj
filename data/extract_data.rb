file_names = [
	'1376661774_UpdatePath_LeftRight_boardsize_100_numberOfGames_1000_maxNum_100_iterStart_50_iterEnd_2500_iterStep_50'
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


