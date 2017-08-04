!include LogicLib.nsh
!include MUI.nsh
!define MUI_TEXT_LICENSE_TITLE "License Agreement"
!define MUI_TEXT_SUBTITLE ""
!define UNINST_KEY "Software\Microsoft\Windows\CurrentVersion\Uninstall\OrigamiSMTP"
!define SFT_VERSION "0.13.RC"
!insertmacro MUI_PAGE_LICENSE "license.txt"
!insertmacro MUI_PAGE_INSTFILES


Outfile "Origami Setup.exe"

InstallDir "$PROGRAMFILES\Origami SMTP"

Name "Origami SMTP"

#Default section
Section


SetShellVarContext all

SetOutPath $INSTDIR


#Check Java Version
# read the value from the registry into the $0 register
#ReadRegStr $0 HKLM "SOFTWARE\JavaSoft\Java Runtime Environment" CurrentVersion

# print the results in a popup message box
#MessageBox MB_OK "version: $0"

CreateDirectory "$APPDATA\Origami SMTP"

#Install the files

File "Origami SMTP.exe"

WriteUninstaller $INSTDIR\uninstaller.exe

# Add Start Menu Items

CreateDirectory "$SMPROGRAMS\Origami SMTP"

#Change working directory temporarily for working directory of shortcut and license file
SetOutPath "$APPDATA\Origami SMTP"
File "license.txt"

CreateShortCut "$SMPROGRAMS\Origami SMTP\Origami SMTP.lnk" "$INSTDIR\Origami SMTP.exe" "origami.ico"

#Change working directory back

SetOutPath $INSTDIR

CreateShortCut "$SMPROGRAMS\Origami SMTP\Uninstall.lnk" "$INSTDIR\uninstaller.exe"

#Add Trusted Root
File "Origami_CA.crt"
File "license.txt"
Push "$INSTDIR\Origami_CA.crt"
Call AddCertificateToStore
Pop $0
${If} $0 != success
	MessageBox MB_OK "Root certificate import failed: $0"
${EndIf}


WriteRegStr HKLM "${UNINST_KEY}" "DisplayName" "Origami SMTP"
				 
WriteRegStr HKLM "${UNINST_KEY}" "UninstallString" "$\"$INSTDIR\uninstaller.exe$\""

WriteRegStr HKLM "${UNINST_KEY}" "DisplayVersion" "${SFT_VERSION}"

WriteRegStr HKLM "${UNINST_KEY}" "RegOwner" "Travis Pessetto"

WriteRegStr HKLM "${UNINST_KEY}" "NoModify" 1

WriteRegStr HKLM "${UNINST_KEY}" "NoRepair" 1

WriteRegStr HKLM "${UNINST_KEY}" "Publisher" "Travis Pessetto"

;Delete "$INSTDIR\Origami_CA.crt"

SectionEnd

Section "Uninstall"

SetShellVarContext all

Delete "$INSTDIR\uninstaller.exe"

#remove certificate
#Push "$INSTDIR\Origami_CA.crt"
#Call un.RemoveCertFromStore
#Pop $0
#${If} $0 != success
	MessageBox MB_OK "Could not remove the Origami CA root certificate.  Manually delete.";
#${EndIf}

Delete "$INSTDIR\Origami_CA.crt"

Delete "$INSTDIR\Origami SMTP.exe"

RMDir $INSTDIR

RMDir /r "$APPDATA\Origami SMTP"

Delete "$SMPROGRAMS\Origami SMTP\Origami SMTP.lnk"

Delete "$SMPROGRAMS\Origami SMTP\Uninstall.lnk"

RMDir "$SMPROGRAMS\Origami SMTP"


DeleteRegKey HKLM "Software\Microsoft\Windows\CurrentVersion\Uninstall\OrigamiSMTP"

SectionEnd

!define CERT_QUERY_OBJECT_FILE 1
!define CERT_QUERY_CONTENT_FLAG_ALL 16382
!define CERT_QUERY_FORMAT_FLAG_ALL 14
!define CERT_STORE_PROV_SYSTEM 10
!define CERT_STORE_OPEN_EXISTING_FLAG 0x4000
!define CERT_SYSTEM_STORE_LOCAL_MACHINE 0x20000
!define CERT_STORE_ADD_ALWAYS 4
 
Function AddCertificateToStore
 
  Exch $0
  Push $1
  Push $R0
 
  System::Call "crypt32::CryptQueryObject(i ${CERT_QUERY_OBJECT_FILE}, w r0, \
    i ${CERT_QUERY_CONTENT_FLAG_ALL}, i ${CERT_QUERY_FORMAT_FLAG_ALL}, \
    i 0, i 0, i 0, i 0, i 0, i 0, *i .r0) i .R0"
 
  ${If} $R0 <> 0
 
    System::Call "crypt32::CertOpenStore(i ${CERT_STORE_PROV_SYSTEM}, i 0, i 0, \
      i ${CERT_STORE_OPEN_EXISTING_FLAG}|${CERT_SYSTEM_STORE_LOCAL_MACHINE}, \
      w 'ROOT') i .r1"
 
    ${If} $1 <> 0
 
      System::Call "crypt32::CertAddCertificateContextToStore(i r1, i r0, \
        i ${CERT_STORE_ADD_ALWAYS}, i 0) i .R0"
      System::Call "crypt32::CertFreeCertificateContext(i r0)"
 
      ${If} $R0 = 0
 
        StrCpy $0 "Unable to add certificate to certificate store"
 
      ${Else}
 
        StrCpy $0 "success"
 
      ${EndIf}
 
      System::Call "crypt32::CertCloseStore(i r1, i 0)"
 
    ${Else}
 
      System::Call "crypt32::CertFreeCertificateContext(i r0)"
 
      StrCpy $0 "Unable to open certificate store"
 
    ${EndIf}
 
  ${Else}
 
    StrCpy $0 "Unable to open certificate file"
 
  ${EndIf}
 
  Pop $R0
  Pop $1
  Exch $0
 
FunctionEnd

Function un.RemoveCertFromStore

#Open the store
#Store should be opened in $1
System::Call "crypt32::CertOpenStore(i ${CERT_STORE_PROV_SYSTEM}, i 0, i 0, \
      i ${CERT_STORE_OPEN_EXISTING_FLAG}|${CERT_SYSTEM_STORE_LOCAL_MACHINE}, \
      w 'ROOT') i .r1"
#Get the certificate by subject
#Compare certificates to make sure they are OK
#Remove certificate from store
#Close the store

FunctionEnd