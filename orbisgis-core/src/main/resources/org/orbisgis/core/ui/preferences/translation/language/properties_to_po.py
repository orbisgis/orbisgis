#encoding : utf8

reference="orbisgis.properties"
referencepo="orbisgis_fr_FR.po"
outfile="orbisgis_fr_FR_generated.po"

def main(reference,referencepo,outfile):
    ref_fich=open(reference,"r")
    corr_d={}
    for lign in ref_fich:
        if "=" in lign:
            splited=lign.strip().split("=")
            corr_d[splited[0]]=splited[1]
    ref_fich.close()
    
    refpo_fich=open(referencepo,"r")
    outpo_file=open(outfile,"w")
    last_msgid=""
    for lign in refpo_fich:
        if "msgid" in lign:
            last_msgid=lign[7:-1].replace('"',"")
        elif "msgstr" in lign:
            localmsg=corr_d.get(last_msgid)
            if not localmsg is None:
                lign=lign[0:6]+' "%s"\n' % (localmsg)
        outpo_file.write(lign)
        
main(reference,referencepo,outfile)

