#!/usr/bin/python3

import os
import argparse

arg_parser = argparse.ArgumentParser()
arg_parser.add_argument('image', type=str, help='original image file name')
arg_parser.add_argument('width', type=int, help='xxxhdpi width')
arg_parser.add_argument('height', type=int, help='xxxhdpi height')
arg_parser.add_argument('--ambient', action='store_true', help='preserve color palette for ambient mode')
args = arg_parser.parse_args()

base_dir = f'{args.image}-resized'

if not os.path.exists(base_dir):
    os.mkdir(base_dir)
if not os.path.exists(f'{base_dir}/drawable-ldpi'):
    os.mkdir(f'{base_dir}/drawable-ldpi')
if not os.path.exists(f'{base_dir}/drawable-mdpi'):
    os.mkdir(f'{base_dir}/drawable-mdpi')
if not os.path.exists(f'{base_dir}/drawable-hdpi'):
    os.mkdir(f'{base_dir}/drawable-hdpi')
if not os.path.exists(f'{base_dir}/drawable-xhdpi'):
    os.mkdir(f'{base_dir}/drawable-xhdpi')
if not os.path.exists(f'{base_dir}/drawable-xxhdpi'):
    os.mkdir(f'{base_dir}/drawable-xxhdpi')
if not os.path.exists(f'{base_dir}/drawable-xxxhdpi'):
    os.mkdir(f'{base_dir}/drawable-xxxhdpi')

if args.ambient:
    magick_command = '-sample'
else:
    magick_command = '-resize'
    #magick_args = '-filter Cubic'

os.system(f'magick convert "{args.image}" {magick_command} {args.width * 3 // 16}x{args.height * 3 // 16} "{base_dir}/drawable-ldpi/{args.image}"')
os.system(f'magick convert "{args.image}" {magick_command} {args.width // 4}x{args.height // 4}           "{base_dir}/drawable-mdpi/{args.image}"')
os.system(f'magick convert "{args.image}" {magick_command} {args.width * 3 // 8}x{args.height * 3 // 8}   "{base_dir}/drawable-hdpi/{args.image}"')
os.system(f'magick convert "{args.image}" {magick_command} {args.width // 2}x{args.height // 2}           "{base_dir}/drawable-xhdpi/{args.image}"')
os.system(f'magick convert "{args.image}" {magick_command} {args.width * 3 // 4}x{args.height * 3 // 4}   "{base_dir}/drawable-xxhdpi/{args.image}"')
os.system(f'magick convert "{args.image}" {magick_command} {args.width}x{args.height}                     "{base_dir}/drawable-xxxhdpi/{args.image}"')
