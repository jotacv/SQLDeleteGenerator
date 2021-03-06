# Written by jotacv
# Generates SQL sentences based on a input Insert style sql file

import re
import time
from string import uppercase

def main():
    
    route="C:\\Users\\jjcalderon_ext\\Desktop\\SCRIPTS_DAO\\"
    fileIn="findSPProductOfferingResSpecByLogisticCode"
    ext=".sql"
    fileOut=fileIn+" - DELETE"
    USER_ID = "900006"
    
    lines = open(route+fileIn+ext).read().split("\n")
    
    daos=[]
    dao=[]
    table_name=""
    column_name=""
    column_value=""
    mem=""
    commentFlag=False
    comment=""
    
    for line in lines:
        line=line.upper()
        if line.startswith("*/"):
            commentFlag=False;
        if commentFlag:
            m = re.search('Date', line)
            if not m:
                comment=comment+line+"\n"
        else:
            if line.startswith("----"):
                if len(dao)>0:
                    daos.append((mem,dao))
                mem=line
                dao=[]
            else:
                comm=False;
                if line.startswith("--"):
                    comm=True
                m = re.search('INSERT\sINTO\s(.*)\s?\(([A-Z_0-9]*),', line)
                if m:
                    table_name=m.group(1)
                    column_name=m.group(2)
                m = re.search("VALUES\s?\('?([0-9]*)'?,", line)
                if m:
                    column_value=m.group(1)
                    print table_name
                    print column_name
                    print column_value
                    dao.append((table_name,column_name,column_value,comm))
        if line.startswith("/*"):
            commentFlag=True;

    daos.append((mem,dao))
    
    fout=open(route+fileOut+ext,'w')
#     fout.write("/*\n")
#     fout.write("Generated by SQLDeleteGenerator\n")
#     fout.write(comment)
#     fout.write("Date: {}\n".format(time.strftime("%d/%m/%Y")))
#     fout.write("*/\n\n")           
    for dao in reversed(daos):
        fout.write("\n"+dao[0]+"\n")
        for table in reversed(dao[1]):
            if table[3]:
                fout.write("-- DELETE FROM {} WHERE {} = {} AND USER_ID_CREATOR_PARTY = {};\n".format(table[0],table[1],table[2], USER_ID))
            else:
                fout.write("DELETE FROM {} WHERE {} = {} AND USER_ID_CREATOR_PARTY = {};\n".format(table[0],table[1],table[2], USER_ID))
#             if table[3]:
#                 fout.write("-- DELETE FROM {} WHERE {} = {} ;\n".format(table[0],table[1],table[2]))
#             else:
#                 fout.write("DELETE FROM {} WHERE {} = {} ;\n".format(table[0],table[1],table[2]))
        fout.write("COMMIT;\n")
    fout.close()
        
    
            
    
    
    
main()
