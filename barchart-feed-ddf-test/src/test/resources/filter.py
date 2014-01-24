#!/usr/bin/python

import sys
import argparse

# Defaults
symbols = []
include = []
exclude = []
logfile = sys.stdin
outfile = lambda s: sys.stdout
start = None
end = None

parser = argparse.ArgumentParser()
parser.add_argument("symbol", nargs="*",
                  help="The symbol to filter by. Multiple symbols can be specified.")
parser.add_argument("-f", "--file", dest="filename",
                  help="The feed log file (default STDIN).")
parser.add_argument("-o", "--out", dest="outfile",
                  help="The filtered output log file (default STDOUT). If a '%%s' is part of the path, it will be replaced with the current record's symbol.")
parser.add_argument("--start", dest="start",
                  help="The start of the date range to return, in the format 'yyyyMMddHHmmssSSS'.")
parser.add_argument("--end", dest="end",
                  help="The end of the date range to return, in the format 'yyyyMMddHHmmssSSS'.")
parser.add_argument("-i", "--include", dest="include", action="append",
                  help="A DDF message type to include. If this is defined, all message types not specifically included will be skipped. ex. '--include=2,7'")
parser.add_argument("-x", "--exclude", dest="exclude", action="append",
                  help="A DDF message type to exclude. ex. '--exclude=2,8'")

args = parser.parse_args()

symbols = args.symbol

if args.start is not None:
	start = int(args.start)

if args.end is not None:
	end = int(args.end)

if args.filename:
	logfile = open(args.filename, 'r');

if args.outfile:
	# Outfile is a template, need to output per-symbol
	if args.outfile.find('%%s'):
		outfiles = {}
		def _outfile(s):
			if not s in outfiles:
				outfiles[s] = open(args.outfile % s, 'w')
			return outfiles[s]
		outfile = _outfile;
	else:
		f = open(args.outfile, 'w');
		outfile = lambda s: f

if args.exclude:
	exclude = args.exclude

if args.include:
	include = args.include

inrange = True
if start is not None or end is not None:
	inrange = False;

for line in logfile:

	if line.find(chr(2)) == -1:
		# Not a market message, throw out timestamps
		continue

	comma = line.find(',')
	sym = line[2:comma]
	if len(symbols) and not sym in symbols:
		continue

	msg = line[1:2] + line[comma:comma+2]
	if len(include):
		if not msg in include:
			continue;
	elif msg in exclude:
		continue

	etx = line.find(chr(3))

	# Parse date if available
	ts = None
	if etx == len(line) - 20:
		ts = int(line[-19:-2])

	if ts is None:
		# Last loop wasn't in date range and this message has no timestamp
		if not inrange:
			continue
	else:
		inrange = (start is None or ts >= start) and (end is None or ts <= end)

	if inrange:
		outfile(sym).write(line)
