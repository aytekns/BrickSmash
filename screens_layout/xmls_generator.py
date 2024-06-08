import re
import pandas as pd
from xml.etree.ElementTree import Element, SubElement, tostring
from xml.dom import minidom


def prettify(elem):
    # Function to prettify the XML output
    rough_string = tostring(elem, 'utf-8')
    re_parsed = minidom.parseString(rough_string)
    return re_parsed.toprettyxml(indent="    ", encoding="UTF-8")


def extract_number(s, char):
    # Find all occurrences of the character followed by any number of digits
    matches = re.findall(f'{char}(-?\\d+)', s)
    # Return the first occurrence of a number or default if none found
    if matches:
        return matches[0]
    elif char == 'c' or char == 'e':
        return '0'
    elif char == 's':
        return '1'
    else:
        return '-1'


def convert_excel_file():
    # Read the Excel file
    excel_file = 'ScreensLayout.xlsx'
    sheets = pd.read_excel(excel_file, sheet_name=None, header=None)

    # Iterate over each sheet except the last one
    for sheet_name in list(sheets)[:-1]:
        data = sheets[sheet_name]
        root = Element('Bricks')

        # Iterate over the first 20 rows and first 9 columns
        for i, row in data.iloc[:20, :9].iterrows():
            for j, cell in enumerate(row):
                cell_value = str(cell).lower()
                brick = SubElement(root, 'Brick')
                brick.set('row', str(i))
                brick.set('col', str(j))

                # Check if the cell is not empty and contains all required characters
                if cell_value and all(char in cell_value for char in 'cspe'):
                    brick.set('color', extract_number(cell_value, 'c'))
                    brick.set('strength', extract_number(cell_value, 's'))
                    brick.set('content', extract_number(cell_value, 'p'))
                    brick.set('explosive', extract_number(cell_value, 'e'))
                else:
                    # If the cell is empty, set all attributes to '0'
                    brick.set('color', '0')
                    brick.set('strength', '0')
                    brick.set('content', '-1')
                    brick.set('explosive', '0')

        # Write to an XML file
        xml_str = prettify(root)
        with open(f'{sheet_name}.xml', 'wb') as xml_file:
            xml_file.write(xml_str)

    print("XML files have been created for each sheet except the last one.")


if __name__ == '__main__':
    # Press the green button in the gutter to run the script.
    convert_excel_file()

# See PyCharm help at https://www.jetbrains.com/help/pycharm/
