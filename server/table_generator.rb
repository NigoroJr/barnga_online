#!/usr/bin/env ruby

# Generate table for team and food visibility
#
# TODO: control randomness of invisible
#
# Author: Naoki Mizuno

require 'optparse'

def generate(teams, self_safe: true, tolerance: 0)
  res = []
  teams.times do
    res << []
    teams.times do |j|
      num = rand(teams + tolerance) - 1
      num = j if num >= teams
      res.last << num
    end
  end

  if self_safe
    teams.times do |i|
      res[i][i] = i
    end
  end

  res
end

def generate_eatability(teams, self_safe)
  res = []
  teams.times do
    res << []
    teams.times do
      # 0 or 1
      num = rand(2)
      res.last << num
    end
  end

  if self_safe
   teams.times do |i|
      res[i][i] = 1
    end
  end

  res
end

def pretty_print(table, var_name = nil)
  printf '%s = ', var_name unless var_name.nil?
  puts '['
  table.each do |row|
    col_str = row.map { |col| format ' %2d', col }
    printf "  [%s],\n", col_str.join(',')
  end
  puts ']'
end

def help
  puts <<-EOF
  Usage: #{File.basename($PROGRAM_NAME)} [options]
  -n=4      Number of teams
  --no-self Don't guarantee self-visibility/eatability
  -t=0      Tolerance
      Allows you to control how much randomness is introduced in assigning
      teams. The higher the tolerance, the less random the results.
      The following table shows the probability when the number of teams is 4
      and the tolerance is 2. Suppose the row (team looking at the other team)
      is 2 and the column (team being looked at) is 3.

      | -1| 0 | 1 | 2 | 3 | 4 | 5 |
      |<----------------->| ORGNL |

      Let the random number generated be N. If N is between -1 and 3
      inclusive, team 2 will see team 3 as team N (invisible if N is -1).
      Otherwise, team 2 will see team 3 as team 3.
  EOF
  exit
end

args = ARGV.getopts('', 'n:4', 'no-self', 'tolerance:0', 'h')
help if args['h']
teams = args['n'].to_i
# Guarantee that you can see your own team and food
self_safe = !args['no-self']
# Control how much to follow the original
tol = args['tolerance'].to_i

p_vis = generate(teams, self_safe: self_safe, tolerance: tol)
f_vis = generate(teams, self_safe: self_safe, tolerance: tol)
f_eat = generate_eatability(teams, self_safe: self_safe)

pretty_print(p_vis, 'player')
puts
pretty_print(f_vis, 'food_visible')
puts
pretty_print(f_eat, 'food_eatable')
