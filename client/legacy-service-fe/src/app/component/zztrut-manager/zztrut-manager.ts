// Esempio di Reactive forms : posso utilizzare i form dinamici
// Reactive formsimport { Component } from '@angular/core';
import { Component, signal, WritableSignal } from '@angular/core';
import { FormArray, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { FormBuilder } from '@angular/forms';
import { MessageHelperService } from '../../services/message-helper.service';
import { BkService, FFDResult, ProgramCallRequest, ProgramCallResponse } from '../../services/bk.service';
import {CommonModule} from '@angular/common';

export class dds_field {
  name: string = '';
  description: string = '';
  value: string = '';
  type: string = '';
  len: number = 0;
  scale: number = 0;
  control: FormControl | undefined;
  getFullDescription(): string {
    let res = this.description + '[' + this.type + '-' + this.len;
    if (this.type === 'S' || this.type === 'P')
      res += ',' + this.scale;
    res += "]";
    return res;
  }
}


@Component({
  selector: 'app-zztrut-manager',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './zztrut-manager.html',
  styleUrl: './zztrut-manager.css',
})

export class ZztrutManager {

  m_dsinput_fields: dds_field[] = [];
  m_dsouput_fields: dds_field[] = [];
  m_call_result: WritableSignal<string> = signal('');

  m_show_additional_parameters: boolean = false;
  m_historyCall: ProgramCallRequest[] = [];
  m_seconds: number = 0;
  main_form: FormGroup;

  constructor(private formBuilder: FormBuilder, private bkService: BkService, private message_service: MessageHelperService) {
    this.main_form = this.formBuilder.group({
      m_program: ['', Validators.required],
      m_tipo: [''],
      m_comando: [''],
      m_flag_io: [''],
      m_dsin: [''],
      m_dsout: [''],
      m_dsinput_field_controls: this.formBuilder.array([])
    });

    this.bkService.retrieveHistoryCall().subscribe(
      data => {
        this.m_historyCall = data;
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta ffd');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta retrieveHistoryCall');
      }
    );
  }

  public get dsinput_field_controls(): FormArray {
    let fa: FormArray = this.main_form.get('m_dsinput_field_controls') as FormArray
    return fa;
  }
  public dsin(): string {
    let val: string = this.main_form.get('m_dsin')?.value as string;
    return val;
  }
  public dsout(): string {
    let val: string = this.main_form.get('m_dsout')?.value as string;
    return val;
  }
  showAdditionalParameters() {
    this.m_show_additional_parameters = !this.m_show_additional_parameters;
  }
  callHasError() {
    return this.m_call_result().length > 0 && this.m_call_result().substring(0, 1) !== '0';
  }
  showDsInput() {
    return this.dsin().length > 0 && this.m_dsinput_fields.length > 0;
  }
  showDsOutput() {
    return this.dsout().length > 0 && !this.callHasError();
  }

  callProgram() {

    this.m_dsouput_fields = [];
    this.m_call_result.set('');

    console.log('m_program is', this.main_form.get('m_program')?.value);
    console.log('[1] - m_dsinput_fields is', this.m_dsinput_fields);
    this.m_dsinput_fields.forEach((element) => {
      element.value = element.control?.value;
    });
    console.log('[2] - m_dsinput_fields is', this.m_dsinput_fields);

    let callReq: ProgramCallRequest = new ProgramCallRequest();
    callReq.program = this.main_form.get('m_program')?.value?.toUpperCase() || 'pgm?????';
    callReq.type = this.main_form.get('m_tipo')?.value?.toUpperCase() || ' ';
    callReq.command = this.main_form.get('m_comando')?.value?.toUpperCase() || '        ';
    callReq.flagIO = this.main_form.get('m_flag_io')?.value?.toUpperCase() || ' ';
    callReq.dsin = this.main_form.get('m_dsin')?.value?.toUpperCase() || '        ';
    callReq.dsout = this.main_form.get('m_dsout')?.value?.toUpperCase() || '        ';


    this.m_dsinput_fields.forEach((ele) => {

      let newFld: { name: string, type: string, length: number, scale: number, value: string, description: string } =
        { name: ele.name.toUpperCase(), type: ele.type.toUpperCase(), length: ele.len, scale: ele.scale, value: ele.value, description: ele.description };
      callReq.values.push(newFld);
    });

    var startDate = new Date();
    this.bkService.callProgram(callReq).subscribe(
      data => {
        let callRes: ProgramCallResponse = data;
        console.log('Program call result is', callRes);
        this.m_call_result.set(callRes.result);
        this.addCallToHistory(callReq);
        var endDate = new Date();
        this.m_seconds = (endDate.getTime() - startDate.getTime()) / 1000;
        console.log('Program call result is', this.m_call_result, 'in seconds', this.m_seconds);

        callRes.values?.forEach((element, index) => {
          let newField: dds_field = new dds_field();
          newField.name = element.name;
          newField.value = element.value;
          newField.type = element.type;
          newField.len = element.length;
          newField.scale = element.scale;
          newField.description = element.description;
          this.m_dsouput_fields.push(newField);
        })
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta ffd');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta callProgram');
      }
    );
  }

  addCallToHistory(aCall: ProgramCallRequest) {
    const copy: ProgramCallRequest[] = this.m_historyCall.slice();
    if (!aCall.program.includes('???')) {
      this.m_historyCall = [];
      this.m_historyCall.push(aCall);
      for (let i = 0; i < copy.length; i++) {
        let reqCall: ProgramCallRequest = new ProgramCallRequest();
        Object.assign(reqCall, copy[i]);
        console.log('reqCall', reqCall)
        console.log('aCall', aCall)
        if (reqCall.toString() !== aCall.toString())
          this.m_historyCall.push(reqCall);
      }
    }
  }

  showDDSIn() {
    // this.m_dsinput_fields = [];
    // this.dsinput_field_controls.clear();
    this.bkService.getFFD('', this.dsin()).subscribe(
      data => {
        let ffd_result: FFDResult = data;
        if (ffd_result.fields === null || ffd_result.fields === undefined || ffd_result.fields?.length < 1 /*  non ci sono campi della dds  */)
          return;
        // Aggiungo i campi della dds di input solo se non c'è già la stessa dds visualizzata
        // Per verificare se e' la stessa dds confronto i nome del primo campo
        if (this.m_dsinput_fields === null
          || this.m_dsinput_fields === undefined
          || this.m_dsinput_fields.length < 1
          || this.m_dsinput_fields[0].name !== ffd_result.fields[0].fieldName) {
          this.m_dsinput_fields = [];
          this.dsinput_field_controls.clear();

          console.log(ffd_result);
          ffd_result.fields?.forEach((element, index) => {
            let newField: dds_field = new dds_field();
            newField.name = element.fieldName;
            newField.description = element.fieldDescription;
            newField.value = '';
            newField.type = element.fieldType;
            newField.len = element.fieldLength;
            newField.scale = element.fieldScale;
            newField.control = this.formBuilder.control('');
            this.m_dsinput_fields.push(newField);
            this.dsinput_field_controls.push(newField.control)
            // console.log('newField is', newField);
          })
        }
      },
      err => {
        console.log('errore in fase di esecuzione della richiesta ffd');
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di esecuzione della richiesta ffd');
      }
    );
  }



  showDDSInFromHistory(aCall: ProgramCallRequest) {
    this.m_dsinput_fields = [];
    this.dsinput_field_controls.clear();
    aCall.values.forEach((element, index) => {
      let newField: dds_field = new dds_field();
      newField.name = element.name;
      newField.description = element.description;
      newField.type = element.type;
      newField.len = element.length;
      newField.scale = element.scale;
      newField.control = this.formBuilder.control(element.value);
      this.m_dsinput_fields.push(newField);
      this.dsinput_field_controls.push(newField.control)
    }
    );
  }

  getCallFromHistory(aCall: ProgramCallRequest) {
    console.log('getCallFromHistory', aCall);
    this.main_form.controls['m_program'].setValue(aCall.program.trim());
    this.main_form.controls['m_tipo'].setValue(aCall.type.trim());
    this.main_form.controls['m_comando'].setValue(aCall.command.trim());
    this.main_form.controls['m_flag_io'].setValue(aCall.flagIO.trim());
    this.main_form.controls['m_dsin'].setValue(aCall.dsin.trim());
    this.main_form.controls['m_dsout'].setValue(aCall.dsout.trim());
    this.showDDSInFromHistory(aCall);
  }

  getCallRequestDescription(aCall: ProgramCallRequest) {
    let res = aCall.program;
    if (aCall.command.trim().length > 0)
      res += '-' + aCall.command.trim();
    if (aCall.dsin.trim().length > 0)
      res += '-' + aCall.dsin.trim();
    if (aCall.dsout.trim().length > 0)
      res += '-' + aCall.dsout.trim();
    return res;
  }
  getSerializedInput(aCall: ProgramCallRequest): string {
    let res: string = '';
    aCall.values.forEach(({
      name,
      type,
      length,
      scale,
      value,
      description
    }, index) => {
      res += value;
    });
    return res;
  }
  deleteCallFromHistory(aCall: ProgramCallRequest) {
    console.log('[0] deleteCallFromHistory', this.m_historyCall);
    // this.m_historyCall=this.m_historyCall.slice(i,i);
    this.m_historyCall = this.m_historyCall.filter(item => item !== aCall)
    console.log('[1] deleteCallFromHistory', this.m_historyCall);
    this.bkService.saveHistoryCall(this.m_historyCall).subscribe(
      data => {
        console.log('deleteCallFromHistory. Saving history call');
      },
      err => {
        this.message_service.messageShow(this.message_service.msg_type.Error, 'Errore in fase di salvataggio della history call');
      }
    );

  }
}
