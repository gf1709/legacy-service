import { Component, signal, WritableSignal } from '@angular/core';
import { BkService, FFDResult } from '../../services/bk.service';
import { MessageHelperService } from '../../services/message-helper.service';
import { CommonModule } from '@angular/common';
import { FormsModule, NgForm } from '@angular/forms';

@Component({
  selector: 'app-java-class-generator',
  imports: [FormsModule, CommonModule],
  templateUrl: './java-class-generator.html',
  styleUrl: './java-class-generator.css',
})
export class JavaClassGenerator {
  m_library: string = '';
  m_file: string = '';
  m_dds_lines: WritableSignal<string[]> = signal([]);

  constructor(private bkService: BkService, private message_service: MessageHelperService) {
  }
  createJavaClass() {
    console.log('creating java class for library', this.m_library, 'and file', this.m_file);
    this.m_dds_lines.set([]);

    this.bkService.getFFD(this.m_library, this.m_file).subscribe(
      data => {
        let ffd_result: FFDResult = data;
        console.log('ffd result is ', ffd_result);
        if (ffd_result.fields === null || ffd_result.fields === undefined || ffd_result.fields?.length < 1 /*  non ci sono campi della dds  */)
          return;
        let lines: string[] = [];
        let ddsname = ffd_result.ddsName?.toUpperCase().replace('$', 'd').replace('§', 'p').replace('£', 'l');

        lines.push('import java.math.BigDecimal;');
        lines.push('import java.math.BigInteger;');
        lines.push('import java.time.LocalDate;');
        lines.push('import java.time.LocalTime;');
        lines.push('import it.allitude.corebanking.monitor.common.protocol.isy.DSDef;');
        lines.push(' ');

        lines.push('public class ' + ddsname + ' extends DSDef {');

        lines.push('    public ' + ddsname + '() {');
        lines.push('    }');

        lines.push('    public ' + ddsname + '(DSDef data) {');
        lines.push('        super(data);');
        lines.push('    }');

        lines.push(' ');
        lines.push('    @Override');
        lines.push('    public void initialize() {');
        lines.push('        setName("' + ffd_result.ddsName?.toUpperCase() + '");');
        lines.push('        setIdf("' + ffd_result.idf + '");');

        for (let field of ffd_result.fields) {
          let fieldName = field.fieldName.toUpperCase().replace('$', 'd').replace('§', 'p').replace('£', 'l');
          let line = '';
          if (field.fieldType === 'A') {
            line = '        addString("' + fieldName + '", ' + field.fieldLength + ');';
          } else if (field.fieldType === 'L') {
            line = '        addDate("' + fieldName + '", 10);';
          } else if (field.fieldType === 'T') {
            line = '        addTime("' + fieldName + '");';
          } else if (field.fieldType === 'S') {
            if (field.fieldLength < 19 && field.fieldScale == 0)
              line = '        addInteger("' + fieldName + '", ' + field.fieldLength + ');';
            else
              line = '        addDecimal("' + fieldName + '", ' + field.fieldLength + "," + field.fieldScale + ');';
          } else if (field.fieldType === 'P') {
            if (field.fieldLength < 19 && field.fieldScale == 0)
              line = '        addInteger("' + fieldName + '", ' + field.fieldLength + ');';
            else
              line = '        addPacked("' + fieldName + '", ' + field.fieldLength + "," + field.fieldScale + ');';
          }
          line += '\t// ' + field.fieldDescription;

          lines.push(line);
        }
        lines.push('    }');
        lines.push(' ');


        for (let field of ffd_result.fields) {
          let fieldName = field.fieldName.toUpperCase().replace('$', 'd').replace('§', 'p').replace('£', 'l');
          lines.push('    // ' + field.fieldDescription);
          if (field.fieldType === 'A') {
            lines.push('    public String get' + fieldName + '() { return (String) getFieldValue("' + fieldName + '"); }');
            lines.push('    public void set' + fieldName + '(String value) { setValue("' + fieldName + '", value); }');
          } else if (field.fieldType === 'L') {
            lines.push('    public LocalDate get' + fieldName + '() { return (LocalDate) getFieldValue("' + fieldName + '"); }');
            lines.push('    public void set' + fieldName + '(LocalDate value) { setValue("' + fieldName + '", value); }');
          } else if (field.fieldType === 'T') {
            lines.push('    public LocalTime get' + fieldName + '() { return (LocalTime) getFieldValue("' + fieldName + '"); }');
            lines.push('    public void set' + fieldName + '(LocalTime value) { setValue("' + fieldName + '", value); }');
          } else if (field.fieldType === 'S' || field.fieldType === 'P') {
            if (field.fieldLength < 19 && field.fieldScale == 0) {
              lines.push('    public BigInteger get' + fieldName + '() { return (BigInteger) getFieldValue("' + fieldName + '"); }');
              lines.push('    public void set' + fieldName + '(BigInteger value) { setValue("' + fieldName + '", value); }');
            } else {
              lines.push('    public BigDecimal get' + fieldName + '() { return (BigDecimal) getFieldValue("' + fieldName + '"); }');
              lines.push('    public void set' + fieldName + '(BigDecimal value) { setValue("' + fieldName + '", value); }');
            }
          }
          lines.push(' ');
        }
        lines.push('}');
        this.m_dds_lines.set(lines);
        console.log('lines are ', lines);
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta ffd');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta ffd');
      }
    );
  }


  copyToClipboard() {
    if (this.m_dds_lines().length < 1)
      return;
    let text: string = '';
    this.m_dds_lines().forEach(element => {
      text += element + '\n';
    });
    if (!text) {
      console.log('Nothing to copy');
      return;
    }
    // Try modern API first
    if (navigator.clipboard && window.isSecureContext) {
      try {
        navigator.clipboard.writeText(text).then(
          (value) => {
            console.log('copy done');
          }
        );
      } catch (err) {
        console.warn('Modern clipboard API failed, trying fallback:', err);
      }
    }
  }

}
